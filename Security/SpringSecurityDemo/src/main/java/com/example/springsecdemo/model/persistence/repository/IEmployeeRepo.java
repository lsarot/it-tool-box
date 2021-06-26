package com.example.springsecdemo.model.persistence.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.example.springsecdemo.model.persistence.entity.Employee;

@Repository
public interface IEmployeeRepo extends PagingAndSortingRepository<Employee, Long> {

}
