package com.vishnu.vy.mobilewebapp.ws.io.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vishnu.vy.mobilewebapp.ws.io.entity.AddressEntity;
import com.vishnu.vy.mobilewebapp.ws.io.entity.UserEntity;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
	
	List<AddressEntity> findAllByUserDetails(UserEntity userEntity);

	AddressEntity findByAddressId(String addressId);
}