package com.sample.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.mybatis.model.persistence.entity.Address;
import com.sample.mybatis.model.persistence.mappers_mybatis.AddressMapper;

import java.util.List;

@Service
public class AddressService implements IAddressService {

	
    @Autowired
    private AddressMapper addressMapper;

	@Override
	public Address getByAddressId(int addressId) {
		return addressMapper.getById(addressId);
	}

	@Override
	public List<Address> getAllAddresses() {
		return addressMapper.getAll();
	}

	@Override
	public void addAddress(Address address) {
		addressMapper.insert(address);
	}

	@Override
	public void updateAddress(Address address) {
		addressMapper.update(address);
	}

	@Override
	public void deleteAddress(int addressId) {
		addressMapper.deleteById(addressId);
	}

}
