package com.sample.mybatis.model.persistence.entity;

import java.io.Serializable;
import java.util.List;

public class Provider implements Serializable {

    private int id;
    private String company_name;
    private String contact_tittle;
    private String contact_name;
    private String phone;
    private String fax;
    private String web;
    private String country;
    private String region;
    private String city;
    private String street;
    private String zip;

    private List<Product> products;

    public Provider() {}

    public Provider(String company_name, String contact_tittle, String contact_name, String phone, String fax, String web, String country, String region, String city, String street, String zip) {
        this.company_name = company_name;
        this.contact_tittle = contact_tittle;
        this.contact_name = contact_name;
        this.phone = phone;
        this.fax = fax;
        this.web = web;
        this.country = country;
        this.region = region;
        this.city = city;
        this.street = street;
        this.zip = zip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getContact_tittle() {
        return contact_tittle;
    }

    public void setContact_tittle(String contact_tittle) {
        this.contact_tittle = contact_tittle;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
