package com.appsdeveloperblog.app.ws.service.impl;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.PasswordResetEntity;
import com.appsdeveloperblog.app.ws.io.entity.RoleEntity;
import com.appsdeveloperblog.app.ws.io.repositories.PasswordResetRepository;
import com.appsdeveloperblog.app.ws.io.repositories.RoleRepository;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.security.SecurityConstants;
import com.appsdeveloperblog.app.ws.security.UserPrincipal;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.EmailServiceImpl;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.PasswordResetDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    PasswordResetRepository passwordResetRepository;

    @Autowired
    RoleRepository roleRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {

        if(userRepository.findByEmail(userDto.getEmail()) != null)
            throw new RuntimeException("Email already exists");

        for(int i = 0; i < userDto.getAddresses().size(); i++) {
            AddressDto addressDto = userDto.getAddresses().get(i);
            addressDto.setAddressId(utils.generateAddressId(30));
            addressDto.setUserDetails(userDto);
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        String userId = utils.generateUserId(30);
        userEntity.setUserId(userId);
        userEntity.setEmailVerificationToken(utils.generateToken(userId, SecurityConstants.EXPIRATION_TIME));
        userEntity.setEmailVerificationStatus(false);

        Collection<RoleEntity> roleEntities = new HashSet<>();
        for(String role : userDto.getRoles()) {
            RoleEntity roleEntity = roleRepository.findByName(role);
            if(roleEntity != null) {
                roleEntities.add(roleEntity);
            }
        }

        userEntity.setRoles(roleEntities);

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        emailService.sendMail(returnValue);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);

        return new UserPrincipal(userEntity);

        //return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
        //  userEntity.getEmailVerificationStatus(), true,
        //  true, true, new ArrayList<>()
        //);
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    public List<UserDto> getUsers(int page, int limit) {

        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);

        List<UserEntity> allUsers = usersPage.getContent();
        List<UserDto> returnValue = new ArrayList<>();
        for(UserEntity object : allUsers) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(object, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if(userEntity == null) throw new RuntimeException(userId + " not found");

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Transactional
    @Override
    public UserDto updateUser(String id, UserDto user) {

        UserEntity userEntity = userRepository.findByUserId(id);
        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        userRepository.save(userEntity);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Transactional
    @Override
    public void deleteUser(String id) {

        UserEntity user = userRepository.findByUserId(id);
        if(user == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(user);
    }

    @Override
    public boolean verifyEmailToken(String token) {

        boolean returnValue = false;

        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if(userEntity != null) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if(!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }

        return returnValue;
    }

    @Override
    public boolean requestPasswordReset(String email) {

        boolean returnValue = false;

        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null) return returnValue;

        String token = utils.generateToken(userEntity.getUserId(), SecurityConstants.PASSWORD_EXPIRATION_TIME);

        PasswordResetEntity passwordResetTokenEntity = new PasswordResetEntity();
        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setUserDetails(userEntity);
        passwordResetRepository.save(passwordResetTokenEntity);

        PasswordResetDto passwordResetDto = new PasswordResetDto();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.map(passwordResetTokenEntity, passwordResetDto);

        emailService.sendPasswordResetMail(passwordResetDto);

        if(passwordResetTokenEntity != null && token != "" && token != null) returnValue = true;

        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String password) {

        boolean returnValue = false;

        if(Utils.hasTokenExpired(token)) return returnValue;

        PasswordResetEntity passwordResetEntity = passwordResetRepository.findByToken(token);
        if(passwordResetEntity == null) return returnValue;

        String encodedPassword = bCryptPasswordEncoder.encode(password);

        UserEntity userEntity = passwordResetEntity.getUserDetails();
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        if(savedUserEntity != null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword))
            returnValue = true;

        passwordResetRepository.delete(passwordResetEntity);


        return returnValue;
    }
}
