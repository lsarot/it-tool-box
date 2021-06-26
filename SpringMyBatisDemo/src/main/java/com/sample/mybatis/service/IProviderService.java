package com.sample.mybatis.service;

import java.util.List;

import com.sample.mybatis.model.persistence.entity.Provider;

public interface IProviderService {

    Provider getByProviderId(final int providerId);
    List<Provider> getAllProviders();
    void addProvider(final Provider provider);
    void updateProvider(final Provider provider);
    void deleteProvider(final int providerId);

}
