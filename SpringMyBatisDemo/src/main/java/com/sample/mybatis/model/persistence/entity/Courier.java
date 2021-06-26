package com.sample.mybatis.model.persistence.entity;

import java.io.Serializable;
import java.util.List;

public class Courier implements Serializable {

    private int id;
    private String company_name;
    private String phone;
    private List<Order> orders;

    public Courier() {}

    public Courier(String company_name, String phone) {
        this.company_name = company_name;
        this.phone = phone;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
