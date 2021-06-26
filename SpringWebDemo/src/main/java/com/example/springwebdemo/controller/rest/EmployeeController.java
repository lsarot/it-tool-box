package com.example.springwebdemo.controller.rest;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springwebdemo.model.persistence.entity.Employee;
import com.example.springwebdemo.service.EmployeeService;

@RestController
public class EmployeeController {

	@Autowired
	public EmployeeService empService;
	
	@GetMapping("/get-emp-info-jndi-ds")
	public List<Employee> employeeInformationOne() {
		
		List<Employee> employees = empService.findAll_using_jndi_datasource();
		return employees;
	}
	
	@GetMapping("/get-emp-info-hibernate-ds")
	public List<Employee> employeeInformationTwo() {

		System.out.println("\nTrying some Hibernate Features\n");
		List<Employee> employees = empService.findAll_using_hibernate();
		return employees;
	}
	
	@GetMapping("/mod-emp-info-hibernate-ds")//claro debería ser método delete http
	private void modEmployeeInformation() {

		System.out.println("\nTrying some Hibernate Features\n");
		empService.mod_employee_info();
	}
	
	//-----------------------
	
	@GetMapping("/public/hello")
    public List<String> publicHello() {
		return Arrays.asList("Hello", "World", "from", "Public");
	}
	
    @GetMapping("/private/hello")
    public List<String> privateHello() {
    	System.out.println(empService.sayHelloSecured()); //tiene @PreAuthorize
    	
    	Employee emp = empService.getEmployee(); //tiene @PostAuthorize
    	System.out.println("Employee retrieved from a @PreAuthorize ('#emp.ename == authentication.name') method");
    	System.out.println(emp);
    	
    	return Arrays.asList("Hello", "World", "from", "Private");
    }
    
    //-----------------------
    
    @GetMapping("/say-hi")
    private String sayHi() {
    	return "HOLA";
    }
    
    @RequestMapping("/hello-world")
    String home() {
    	return "Hello World!";
    }
    
}
