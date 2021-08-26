package com.appsdeveloperblog.app.ws.ui.controller;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.Roles;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordRequestResetModel;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordResetModel;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.bytebuddy.description.method.MethodDescription;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.beans.MethodDescriptor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @PostAuthorize("hasRole('ADMIN') or returnObject.userId == principal.userId")
    @GetMapping(path="/{id}")
    public UserRest getUser(@PathVariable String id) {

        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);
        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="Authorization", value="${userController.authorizationHeader.description}", paramType = "header"),
            @ApiImplicitParam(name="UserID", value="User id", paramType = "header"),
            @ApiImplicitParam(name="Content-Type", value="Content-Type", paramType = "header"),
            @ApiImplicitParam(name="Accept", value="accept", paramType = "header")
    })
    @GetMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value="page", defaultValue = "1") int page,
                                @RequestParam(value="limit", defaultValue = "2") int limit) {
        List<UserDto> allUsers = new ArrayList<>();
        allUsers = userService.getUsers(page, limit);

        List<UserRest> returnValue = new ArrayList<>();

        for(UserDto obj : allUsers) {
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(obj, userRest);
            returnValue.add(userRest);
        }

        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException{
        
        UserRest returnValue = new UserRest();

        if(userDetails.getFirstName().isEmpty() || userDetails.getLastName().isEmpty()
                || userDetails.getEmail().isEmpty() || userDetails.getPassword().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));

        UserDto createdUser = userService.createUser(userDto);
        modelMapper.map(createdUser, returnValue);

        return returnValue;
    }

    @PutMapping(path="/{id}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public UserRest updateUser(@RequestBody UserDetailsRequestModel userDetail, @PathVariable String id) {

        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetail, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);

        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == principal.userId")
    //@Secured("ROLE_ADMIN")
    @DeleteMapping(path="/{id}")
    public OperationStatusModel deleteUser(@PathVariable String id) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());
        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationResult.SUCCESS.name());

        return returnValue;
    }

    @GetMapping(path="/{id}/addresses" ,consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
    produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<AddressRest> getUserAddresses(@PathVariable String id, @RequestParam(value="page", defaultValue = "1") int page,
                                              @RequestParam(value="limit", defaultValue = "1") int limit) {

        List<AddressRest> returnValue = new ArrayList<>();

        List<AddressDto> addresses = addressService.getAddresses(id, page, limit);
        if(addresses != null && !addresses.isEmpty()) {

            ModelMapper modelMapper = new ModelMapper();
            Type listType = new TypeToken<List<AddressRest>>() {}.getType();
            returnValue = modelMapper.map(addresses, listType);
        }

        return returnValue;
    }

    @GetMapping(path="/{userId}/addresses/{addressId}" ,consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public AddressRest getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

        AddressRest returnValue = new AddressRest();

        AddressDto address = addressService.getAddressById(userId, addressId);

        if(address != null) {

            ModelMapper modelMapper = new ModelMapper();
            modelMapper.map(address, returnValue);
        }

        return returnValue;
    }

    @GetMapping(path = "/email-verification", produces = {MediaType.APPLICATION_XML_VALUE,
                MediaType.APPLICATION_JSON_VALUE })
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if(isVerified) {
            returnValue.setOperationResult(RequestOperationResult.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestOperationResult.ERROR.name());
        }

        return returnValue;
    }

    @PostMapping(path = "/password-reset-request",
                produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel requestReset(@RequestBody PasswordRequestResetModel passwordRequestResetModel) {

        boolean operationResult = userService.requestPasswordReset(passwordRequestResetModel.getEmail());

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationResult.ERROR.name());

        if(operationResult) {
            returnValue.setOperationResult(RequestOperationResult.SUCCESS.name());
        }

        return returnValue;
    }

    @PostMapping(path = "/password-reset",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(
                passwordResetModel.getToken(),
                passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationResult.ERROR.name());

        if(operationResult) {
            returnValue.setOperationResult(RequestOperationResult.SUCCESS.name());
        }

        return returnValue;
    }
}
