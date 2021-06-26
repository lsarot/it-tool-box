package com.sample.mybatis.model.persistence.entity;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {

    private int id;
    private Date issue_date;
    private Date send_date;
    private Date required_date;
    private int employee_id;
    private int client_id;
    private int courier_id;

    private Employee employee;
    private Client client;
    private Courier courier;


    public Order() {}

    public Order(Date issue_date, Date send_date, Date required_date, int employee_id, int client_id, int courier_id) {
        this.issue_date = issue_date;
        this.send_date = send_date;
        this.required_date = required_date;
        this.employee_id = employee_id;
        this.client_id = client_id;
        this.courier_id = courier_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(Date issue_date) {
        this.issue_date = issue_date;
    }

    public Date getSend_date() {
        return send_date;
    }

    public void setSend_date(Date send_date) {
        this.send_date = send_date;
    }

    public Date getRequired_date() {
        return required_date;
    }

    public void setRequired_date(Date required_date) {
        this.required_date = required_date;
    }

    public int getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getCourier_id() {
        return courier_id;
    }

    public void setCourier_id(int courier_id) {
        this.courier_id = courier_id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

}
