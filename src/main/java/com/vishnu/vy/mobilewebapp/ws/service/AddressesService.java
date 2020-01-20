package com.vishnu.vy.mobilewebapp.ws.service;

import java.util.List;

import com.vishnu.vy.mobilewebapp.ws.shared.dto.AddressDTO;

public interface AddressesService {

	List<AddressDTO> getAddresses(String id);
	AddressDTO getAddress(String addressId);

}
