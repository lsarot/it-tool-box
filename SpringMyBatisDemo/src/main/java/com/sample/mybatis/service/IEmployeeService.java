package com.sample.mybatis.service;

import java.util.List;

import com.sample.mybatis.model.persistence.entity.Contact;
import com.sample.mybatis.model.persistence.entity.Employee;

public interface IEmployeeService {

    Employee getByEmployeeId(final int employeeId);
    List<Employee> getAllEmployees();
    void addEmployee(final Employee employee);
    void updateEmployee(final Employee employee);
    void deleteEmployee(final int employeeId);

}
