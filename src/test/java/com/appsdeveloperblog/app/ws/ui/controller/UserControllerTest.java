package com.appsdeveloperblog.app.ws.ui.controller;

import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.service.impl.UserServiceImpl;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Mock
    UserServiceImpl userService;

    @InjectMocks
    UserController userController;

    UserDto userDto;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.initMocks(this);

        userDto = new UserDto();
        userDto.setFirstName("Miroslav");
        userDto.setLastName("Perovic");
        userDto.setEmail("miroslav.perovic94@gmail.com");
        userDto.setEmailVerificationStatus(Boolean.FALSE);
        userDto.setEmailVerificationToken(null);
        userDto.setUserId("dfheriogherioger");
        userDto.setAddresses(getAddresesDto());
        userDto.setEncryptedPassword("igheroigheo");
    }

    @Test
    void getUser() {

        when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        UserRest userRest = userController.getUser("furhogier");

        assertNotNull(userRest);
        assertEquals(userDto.getUserId(), userRest.getUserId());
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());

    }

    @Test
    void getUsers() {
    }

    @Test
    void createUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void getUserAddresses() {
    }

    @Test
    void getUserAddress() {
    }

    @Test
    void verifyEmailToken() {
    }

    @Test
    void requestReset() {
    }

    @Test
    void resetPassword() {
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
}