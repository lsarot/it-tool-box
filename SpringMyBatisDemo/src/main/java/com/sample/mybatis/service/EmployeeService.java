package com.sample.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.mybatis.model.persistence.entity.Employee;
import com.sample.mybatis.model.persistence.mappers_mybatis.EmployeeMapper;

import java.util.List;

@Service
public class EmployeeService implements IEmployeeService {


    @Autowired
    private EmployeeMapper employeeMapper;


    @Override
    public void addEmployee(final Employee employee) {
        employeeMapper.insert(employee);
    }

    @Override
    public Employee getByEmployeeId(int employeeId) {
        return employeeMapper.getById(employeeId);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeMapper.getAll();
    }

    @Override
    public void updateEmployee(Employee employee) {
        employeeMapper.update(employee);
    }

    @Override
    public void deleteEmployee(int employeeId) {
        employeeMapper.deleteById(employeeId);
    }

}
