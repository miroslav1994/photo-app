package com.appsdeveloperblog.app.ws.io.repositories;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);
    UserEntity findByUserId(String userId);
    UserEntity findUserByEmailVerificationToken(String token);

    @Query(value = "select * from users u " +
                    " where u.email_verification_status = true",
            countQuery = "select count(*) from users u " +
                    " where u.email_verification_status = true",
            nativeQuery = true)
    Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);

    @Query(value="select * from users u where u.first_name = ?1 and u.last_name = ?2", nativeQuery = true)
    List<UserEntity> findUsersByFirstName(String firstName, String lastName);

    @Query(value="select * from users u where u.first_name = :first_name and u.last_name = :last_name", nativeQuery = true)
    List<UserEntity> findUsersByFirstName2(@Param("first_name") String firstName, @Param("last_name") String lastName);

    @Query(value = "select u.first_name, u.last_name from users u where u.first_name LIKE %:keyword% or u.last_name LIKE %:keyword%", nativeQuery = true)
    List<Object[]> findByKeyword(@Param("keyword") String keyword);

    @Transactional
    @Modifying
    @Query(value="update users set emailVerificationStatus = :email_verification_status where userId = :userId")
    void updateUsersVerificationStatus(@Param("email_verification_status") boolean email_verification_status, @Param("userId") String userId);
}

