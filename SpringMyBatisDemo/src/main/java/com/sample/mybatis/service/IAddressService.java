package com.sample.mybatis.service;

import java.util.List;

import com.sample.mybatis.model.persistence.entity.Address;

public interface IAddressService {

    Address getByAddressId(final int addressId);
    List<Address> getAllAddresses();
    void addAddress(final Address address);
    void updateAddress(final Address address);
    void deleteAddress(final int addressId);

}
