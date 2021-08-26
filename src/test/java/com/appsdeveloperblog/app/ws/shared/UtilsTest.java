package com.appsdeveloperblog.app.ws.shared;

import com.appsdeveloperblog.app.ws.security.SecurityConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {

    @Autowired
    Utils utils;

    @BeforeEach
    void setUp() throws Exception {

    }

    @Test
    void generateUserId() {

        String userId = utils.generateUserId(30);
        assertNotNull(userId);
        assertTrue(userId.length() == 30);
    }

    @Test
    @Disabled
    void generateAddressId() {
    }

    @Test
    void hasTokenExpired() {
        String token = utils.generateToken("4yr65hhyid84", SecurityConstants.EXPIRATION_TIME);
        assertNotNull(token);
        boolean tokenExpired = Utils.hasTokenExpired(token);

        assertFalse(tokenExpired);
    }

    @Test
    @Disabled
    void generateToken() {
    }
}