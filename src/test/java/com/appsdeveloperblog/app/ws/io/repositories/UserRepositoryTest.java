package com.appsdeveloperblog.app.ws.io.repositories;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    void findByEmail() {
    }

    @Test
    void findByUserId() {
    }

    @Test
    void findUserByEmailVerificationToken() {
    }

    @Test
    void findAllUsersWithConfirmedEmailAddress() {

        Pageable pageableRequest = PageRequest.of(0, 3);
        Page<UserEntity> pages = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
        assertNotNull(pages);

        List<UserEntity> userEntities = pages.getContent();
        assertNotNull(userEntities);
        assertTrue(userEntities.size() == 2);
    }

    @Test
    void findUsersByFIrstName() {

        String firstName = "Miroslav";
        String lastName = "Perović";
        List<UserEntity> users = userRepository.findUsersByFirstName(firstName, lastName);

        assertNotNull(users);
        UserEntity user = users.get(0);
        assertEquals(firstName, user.getFirstName());
    }

    @Test
    void findByKeyword() {

        String keyword = "ero";
        List<Object[]> users = userRepository.findByKeyword(keyword);

        Object[] user = users.get(0);
        String firstName = String.valueOf(user[0]);
        assertEquals("Miroslav", firstName);
    }

    @Test
    void updateUsersVerificationStatus() {
        String userId = "WQkgwsYbiJ0rMAR90OQjaWnFWrpbu1";
        userRepository.updateUsersVerificationStatus(true, userId);

        UserEntity user = userRepository.findByUserId(userId);
        assertEquals(true, user.getEmailVerificationStatus());
    }
}