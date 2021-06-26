package com.sample.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.mybatis.model.persistence.entity.Provider;
import com.sample.mybatis.model.persistence.mappers_mybatis.ProviderMapper;

import java.util.List;

@Service
public class ProviderService implements IProviderService {


    @Autowired
    private ProviderMapper providerMapper;


    @Override
    public void addProvider(final Provider provider) {
        providerMapper.insert(provider);
    }

    @Override
    public Provider getByProviderId(int providerId) {
        return providerMapper.getById(providerId);
    }

    @Override
    public List<Provider> getAllProviders() {
        return providerMapper.getAll();
    }

    @Override
    public void updateProvider(Provider provider) {
        providerMapper.update(provider);
    }

    @Override
    public void deleteProvider(int providerId) {
        providerMapper.deleteById(providerId);
    }

}
