package com.vishnu.vy.mobilewebapp.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vishnu.vy.mobilewebapp.ws.exception.UserServiceException;
import com.vishnu.vy.mobilewebapp.ws.service.AddressesService;
import com.vishnu.vy.mobilewebapp.ws.service.UserService;
import com.vishnu.vy.mobilewebapp.ws.shared.dto.AddressDTO;
import com.vishnu.vy.mobilewebapp.ws.shared.dto.UserDto;
import com.vishnu.vy.mobilewebapp.ws.ui.model.request.RequestOperationName;
import com.vishnu.vy.mobilewebapp.ws.ui.model.request.UserDetailsRequestModel;
import com.vishnu.vy.mobilewebapp.ws.ui.model.response.AddressesRest;
import com.vishnu.vy.mobilewebapp.ws.ui.model.response.ErrorMessages;
import com.vishnu.vy.mobilewebapp.ws.ui.model.response.OperationStatusModel;
import com.vishnu.vy.mobilewebapp.ws.ui.model.response.RequestOperationStatus;
import com.vishnu.vy.mobilewebapp.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("users") // https://localhost:8080/users/
public class UserController {

	@Autowired
	UserService service;

	@Autowired
	AddressesService addressesService;

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable final String id) {

		if (id.isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_PARAMETER.getErrorMessage());

		final UserRest userResponse = new UserRest();

		UserDto userDto = service.getUserByUserId(id);

		BeanUtils.copyProperties(userDto, userResponse);

		return userResponse;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {

		if (userDetails.getFirstName().isEmpty() || userDetails.getLastName().isEmpty()
				|| userDetails.getEmail().isEmpty() || userDetails.getPassword().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		final ModelMapper modelMapper = new ModelMapper();

		final UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		final UserDto createUser = service.createUser(userDto);

		final UserRest userResponse = modelMapper.map(createUser, UserRest.class);

		return userResponse;
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

		UserRest returnValue = new UserRest();

		UserDto userDto = new UserDto();

		BeanUtils.copyProperties(userDetails, userDto);
//		userDto = new ModelMapper().map(userDetails, UserDto.class);

		UserDto updateUser = service.updateUser(id, userDto);
		BeanUtils.copyProperties(updateUser, returnValue);
//		returnValue = new ModelMapper().map(updateUser, UserRest.class);

		return returnValue;
	}

	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {

		final OperationStatusModel operationStatusModel = new OperationStatusModel();

		operationStatusModel.setOperationName(RequestOperationName.DELETE.name());

		service.deleteUser(id);

		operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

		return operationStatusModel;
	}

	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "2") int limit) {

		List<UserRest> returnValue = new ArrayList<>();

		List<UserDto> users = service.getUsers(page, limit);

		for (UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}

		return returnValue;
	}

	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_ATOM_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public List<AddressesRest> getUserAddresses(@PathVariable final String id) {

		if (id.isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_PARAMETER.getErrorMessage());

		List<AddressesRest> returnListOfAddress = new ArrayList<AddressesRest>();

		final List<AddressDTO> addressesDTO = addressesService.getAddresses(id);

		if (addressesDTO != null && !addressesDTO.isEmpty()) {
			Type listType = new TypeToken<List<AddressesRest>>() {
			}.getType();
			returnListOfAddress = new ModelMapper().map(addressesDTO, listType);
		}

		return returnListOfAddress;

	}

	@GetMapping(path = "/addresses/{addressId}", produces = { MediaType.APPLICATION_ATOM_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public AddressesRest getUserAddress(@PathVariable String addressId) {

		if (addressId.isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_PARAMETER.getErrorMessage());

		final AddressDTO addressesDTO = addressesService.getAddress(addressId);

		final ModelMapper modelMapper = new ModelMapper();

		final AddressesRest returnAddress = modelMapper.map(addressesDTO, AddressesRest.class);

		return returnAddress;

	}

}
