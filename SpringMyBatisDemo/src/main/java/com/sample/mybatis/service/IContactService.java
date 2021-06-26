package com.sample.mybatis.service;

import java.util.List;

import com.sample.mybatis.model.persistence.entity.Contact;

public interface IContactService {

    Contact getByContactId(final int contactId);
    List<Contact> getAllContacts();
    void addContact(final Contact contact);
    void updateContact(final Contact contact);
    void deleteContact(final int contactId);

}
