package com.sample.mybatis.service;

import java.util.List;

import com.sample.mybatis.model.persistence.entity.Client;
import com.sample.mybatis.model.persistence.entity.Contact;

public interface IClientService {

    Client getByClientId(final int clientId);
    List<Client> getAllClients();
    void addClient(final Client client);
    void updateClient(final Client client);
    void deleteClient(final int clientId);

}
