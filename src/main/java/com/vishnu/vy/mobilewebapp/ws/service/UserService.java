package com.vishnu.vy.mobilewebapp.ws.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.vishnu.vy.mobilewebapp.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {

	UserDto createUser(UserDto userDto);
	UserDto getUser(String email);
	UserDto getUserByUserId(String userId);
}
