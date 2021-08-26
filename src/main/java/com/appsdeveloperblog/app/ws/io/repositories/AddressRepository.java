package com.appsdeveloperblog.app.ws.io.repositories;

import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<AddressEntity, Long> {

    Page<AddressEntity> findAllByUserDetails(UserEntity user, Pageable pageable);
    AddressEntity findByUserDetailsAndAddressId(UserEntity user, String addressId);
}
