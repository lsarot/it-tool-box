package com.sample.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.mybatis.model.persistence.entity.Contact;
import com.sample.mybatis.model.persistence.mappers_mybatis.ContactMapper;

import java.util.List;

@Service
public class ContactService implements IContactService {


    @Autowired
    private ContactMapper contactMapper;


    @Override
    public void addContact(final Contact contact) {
        contactMapper.insert(contact);
    }

    @Override
    public Contact getByContactId(int contactId) {
        return contactMapper.getById(contactId);
    }

    @Override
    public List<Contact> getAllContacts() {
        return contactMapper.getAll();
    }

    @Override
    public void updateContact(Contact contact) {
        contactMapper.update(contact);
    }

    @Override
    public void deleteContact(int contactId) {
        contactMapper.deleteById(contactId);
    }

}
