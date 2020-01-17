package com.vishnu.vy.mobilewebapp.ws.ui.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vishnu.vy.mobilewebapp.ws.exception.UserServiceException;
import com.vishnu.vy.mobilewebapp.ws.service.UserService;
import com.vishnu.vy.mobilewebapp.ws.shared.dto.UserDto;
import com.vishnu.vy.mobilewebapp.ws.ui.model.request.UserDetailsRequestModel;
import com.vishnu.vy.mobilewebapp.ws.ui.model.response.ErrorMessages;
import com.vishnu.vy.mobilewebapp.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("users") // https://localhost:8080/users/
public class UserController {

	@Autowired
	UserService service;

	@GetMapping(path = "/{id}", produces = { 
			MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE 
			})
	public UserRest getUser(@PathVariable final String id) {

		final UserRest userResponse = new UserRest();

		UserDto userDto = service.getUserByUserId(id);

		BeanUtils.copyProperties(userDto, userResponse);

		return userResponse;
	}

	@PostMapping(
			consumes = { MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
			produces = { MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
			)
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {

		if(userDetails.getFirstName().isEmpty() 
				|| userDetails.getLastName().isEmpty()
				|| userDetails.getEmail().isEmpty()
				|| userDetails.getPassword().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
		final UserRest userResponse = new UserRest();
		final UserDto userDto = new UserDto();

		BeanUtils.copyProperties(userDetails, userDto);

		UserDto createUser = service.createUser(userDto);

		BeanUtils.copyProperties(createUser, userResponse);

		return userResponse;
	}

	@PutMapping
	public String updateUser() {

		return "update user was called";
	}

	@DeleteMapping
	public String deleteUser() {

		return "delete user was called";
	}

}
