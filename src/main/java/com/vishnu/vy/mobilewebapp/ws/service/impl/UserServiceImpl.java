package com.vishnu.vy.mobilewebapp.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vishnu.vy.mobilewebapp.ws.io.entity.UserEntity;
import com.vishnu.vy.mobilewebapp.ws.io.repository.UserRepository;
import com.vishnu.vy.mobilewebapp.ws.service.UserService;
import com.vishnu.vy.mobilewebapp.ws.shared.Utils;
import com.vishnu.vy.mobilewebapp.ws.shared.dto.AddressDTO;
import com.vishnu.vy.mobilewebapp.ws.shared.dto.UserDto;
import com.vishnu.vy.mobilewebapp.ws.ui.model.response.ErrorMessages;

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
		
		for(int i=0;i<userDto.getAddresses().size();i++)
		{
			AddressDTO address = userDto.getAddresses().get(i);
			address.setUserDetails(userDto);
			address.setAddressId(utils.generateAddressId(30));
			userDto.getAddresses().set(i, address);
		}

		final ModelMapper modelMapper = new ModelMapper();

		final UserEntity userEntity =  modelMapper.map(userDto, UserEntity.class);
		
		System.out.println(userEntity.getAddresses());

		final String publicUserId = utils.generateUserId(30);
		userEntity.setEncryptedPassword(bcryptPassword.encode(userDto.getPassword()));

		userEntity.setUserId(publicUserId);

		final UserEntity savedUserEntity = userRepository.save(userEntity);


		final UserDto userDtoReturnValue =  modelMapper.map(savedUserEntity, UserDto.class);

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
		final UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException(userId);

		BeanUtils.copyProperties(userEntity, userDto);

		return userDto;
	}

	@Override
	public UserDto updateUser(String id, UserDto userDtoObj) {

		final UserDto userDto = new UserDto();
		final UserEntity userEntity = userRepository.findByUserId(id);
		if (userEntity == null)
			throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		userEntity.setFirstName(userDtoObj.getFirstName());
		userEntity.setLastName(userDtoObj.getLastName());

		final UserEntity updatedUserEntity = userRepository.save(userEntity);
		BeanUtils.copyProperties(updatedUserEntity, userDto);

		return userDto;
	}

	@Override
	public void deleteUser(String id) {
		final UserEntity userEntity = userRepository.findByUserId(id);
		if (userEntity == null)
			throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		userRepository.delete(userEntity);

	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {

		final List<UserDto> returnValue = new ArrayList<>();

		if (page > 0)
			page = page - 1;

		Pageable pageableRequest = PageRequest.of(page, limit);

		Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = usersPage.getContent();

		for (UserEntity userEntity : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);
		}

		return returnValue;
	}

}
