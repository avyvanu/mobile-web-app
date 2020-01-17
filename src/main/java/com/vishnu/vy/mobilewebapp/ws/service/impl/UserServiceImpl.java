package com.vishnu.vy.mobilewebapp.ws.service.impl;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vishnu.vy.mobilewebapp.ws.io.entity.UserEntity;
import com.vishnu.vy.mobilewebapp.ws.io.repository.UserRepository;
import com.vishnu.vy.mobilewebapp.ws.service.UserService;
import com.vishnu.vy.mobilewebapp.ws.shared.Utils;
import com.vishnu.vy.mobilewebapp.ws.shared.dto.UserDto;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bcryptPassword;

	@Override
	public UserDto createUser(UserDto userDto) {

		if (userRepository.findByEmail(userDto.getEmail()) != null)
			throw new RuntimeException("User already exits");

		final UserEntity userEntity = new UserEntity();

		BeanUtils.copyProperties(userDto, userEntity);

		final String publicUserId = utils.generateUserId(30);
		userEntity.setEncryptedPassword(bcryptPassword.encode(userDto.getPassword()));

		userEntity.setUserId(publicUserId);

		final UserEntity savedUserEntity = userRepository.save(userEntity);

		final UserDto userDtoReturnValue = new UserDto();

		BeanUtils.copyProperties(savedUserEntity, userDtoReturnValue);

		return userDtoReturnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		final UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		
		final UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);
		final UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userEntity, userDto);
		return userDto;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		
		final UserDto userDto = new UserDto();
		final  UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException(userId);
		
		BeanUtils.copyProperties(userEntity, userDto);
		
		return userDto;
	}

}
