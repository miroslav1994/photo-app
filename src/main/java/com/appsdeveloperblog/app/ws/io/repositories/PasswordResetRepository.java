package com.appsdeveloperblog.app.ws.io.repositories;

import com.appsdeveloperblog.app.ws.io.entity.PasswordResetEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends CrudRepository<PasswordResetEntity, Long> {

    PasswordResetEntity findByToken(String token);
}
