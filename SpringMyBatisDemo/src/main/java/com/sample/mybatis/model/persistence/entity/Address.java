package com.sample.mybatis.model.persistence.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "Address entity", description = "Address representation") //SWAGGER
public class Address implements Serializable {

    @ApiModelProperty(value = "The address's id", required = false) //SWAGGER
    private int id;
    
    @ApiModelProperty(value = "The address's street", required = true)
    private String street;
    
    @ApiModelProperty(value = "The address's contact id", required = true)
    private int contact_id;
    
    private Contact contact;

    
    public Address() {}

    public Address(int id, String street, int contact_id) {
        this.id = id;
        this.street = street;
        this.contact_id = contact_id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

}
