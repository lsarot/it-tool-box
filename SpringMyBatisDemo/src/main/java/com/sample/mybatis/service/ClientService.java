package com.sample.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.mybatis.model.persistence.entity.Client;
import com.sample.mybatis.model.persistence.entity.Contact;
import com.sample.mybatis.model.persistence.mappers_mybatis.ClientMapper;

import java.util.List;

@Service
public class ClientService implements IClientService {


    @Autowired
    private ClientMapper clientMapper;


    @Override
    public void addClient(final Client client) {
        clientMapper.insert(client);
    }

    @Override
    public Client getByClientId(int clientId) {
        return clientMapper.getById(clientId);
    }

    @Override
    public List<Client> getAllClients() {
        return clientMapper.getAll();
    }


    @Override
    public void updateClient(Client client) {
        clientMapper.update(client);
    }

    @Override
    public void deleteClient(int clientId) {
        clientMapper.deleteById(clientId);
    }


}
