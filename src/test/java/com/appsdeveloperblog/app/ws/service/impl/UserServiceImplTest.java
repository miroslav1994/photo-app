package com.appsdeveloperblog.app.ws.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import java.awt.print.Pageable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.appsdeveloperblog.app.ws.io.repositories.PasswordResetRepository;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.EmailServiceImpl;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.bytebuddy.description.method.MethodDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    EmailServiceImpl emailService;

    String userId = "isughwoighweoig";
    String encryptedPassword = "kjdghweioghewg";

    UserEntity userEntity;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.initMocks(this);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Miroslav");
        userEntity.setLastName("Perovic");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmailVerificationToken("gwrgwgwoiewjgioewg");
        userEntity.setEmail("miroslav.perovic94@gmail.com");
        userEntity.setAddresses(getAdressesEntity());

    }

    @Test
    void createUser() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("siohaoidhrouigw");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        List<AddressDto> addresses = getAddresesDto();

        UserDto storedUserDetails = userService.createUser(makeUserDto("Miroslav", "Perovic", "123456789", "miroslav.perovic94@gmail.com"));
        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
        assertNotNull(storedUserDetails.getUserId());
        assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
        verify(utils, times(2)).generateAddressId(30);
        verify(bCryptPasswordEncoder, times(1)).encode("123456789");
    }

    private List<AddressDto> getAddresesDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setType("shipping");
        addressDto.setCity("Vancouver");
        addressDto.setCountry("Canada");
        addressDto.setPostalCode("ABC123");
        addressDto.setStreetName("123 Street home");

        AddressDto billingAddressDto = new AddressDto();
        billingAddressDto.setType("billing");
        billingAddressDto.setCity("Vancouver");
        billingAddressDto.setCountry("Canada");
        billingAddressDto.setPostalCode("ABC123");
        billingAddressDto.setStreetName("123 Street home");

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(addressDto);
        addresses.add(billingAddressDto);

        return addresses;
    }

    private List<AddressEntity> getAdressesEntity() {

        List<AddressDto> addresses = getAddresesDto();

        Type listType = new TypeToken<List<AddressEntity>>(){}.getType();

        return new ModelMapper().map(addresses, listType);
    }

    @Test
    void getUser() {

        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = new UserDto();
        userDto = userService.getUser("test@test");

        assertNotNull(userDto);
        assertEquals("Miroslav", userDto.getFirstName());
    }

    @Test
    final void getUser_UsernameNotFoundException() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                    () -> {
                        userService.getUser("test@test");
                    }
                );
    }

    @Test
    final void testCreateUser_CreatedUserServiceException() {

        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        assertThrows(RuntimeException.class,

                    () -> {
                        userService.createUser(makeUserDto("Miroslav", "Perovic", "123456789", "miroslav.perovic94@gmail.com"));
                    }
                );
    }

    private UserDto makeUserDto(String firstName, String lastName, String password, String email) {

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddresesDto());
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setPassword(password);
        userDto.setEmail(email);

        return userDto;
    }


    @Test
    void getUserByUserId() {

        when(userRepository.findByUserId(anyString())).thenReturn(userEntity);
        UserDto returnedUser = userService.getUserByUserId("iorerhjgoierhg");

        assertNotNull(returnedUser);
        assertEquals(returnedUser.getFirstName(), userEntity.getFirstName());
    }

    @Test
    void updateUser() {

        when(userRepository.findByUserId(anyString())).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDto userDto = userService.updateUser("FDIUDHGOERIGHERG", makeUserDto("Miroslav", "Perovic", "1234567890", "gejriogjer"));

        assertNotNull(userDto);
    }

    @Test
    void deleteUser() {

        when(userRepository.findByUserId(anyString())).thenReturn(userEntity);

        userService.deleteUser("112");
        verify(userRepository, times(1)).delete(userEntity);
    }

    @Test
    void verifyEmailToken() {

        when(userRepository.findUserByEmailVerificationToken(anyString())).thenReturn(userEntity);
        when(Utils.hasTokenExpired("jf9i4jgu83nfl024242")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        boolean verify = userService.verifyEmailToken("jf9i4jgu83nfl024242asd");
    }

    @Test
    void requestPasswordReset() {
    }

    @Test
    void resetPassword() {
    }
}