package com.example.springwebdemo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.springwebdemo.model.persistence.entity.Employee;
import com.example.springwebdemo.model.persistence.repository.EmployeeRepo;

@Service
public class EmployeeService {
	
	@Autowired private EmployeeRepo employeeRepo;
	
	
	public List<Employee> findAll_using_h2_1() {
			return employeeRepo.fetchAll_using_h2_1();
	}
	
	
	public List<Employee> findAll_using_jndi_datasource() {
		return employeeRepo.fetchAll_using_jndi_datasource();
	}
	
	
	public List<Employee> findAll_using_hibernate() {
		return employeeRepo.findAll_using_hibernate();
	}


	public void mod_employee_info() {
		employeeRepo.mod_employee_info();
	}
	
	//-----------------------
	
	/** expression-based access control. WITH SpEL (Spring Expression Language)
	 * The @PreAuthorize can check for authorization before entering into method. The @PreAuthorize authorizes on the basis of role or the argument which is passed to the method.
	 * The @PostAuthorize checks for authorization after method execution. The @PostAuthorize authorizes on the basis of logged in roles, return object by method and passed argument to the method.
	 */
    @PreAuthorize("authenticated")
    public String sayHelloSecured() {
    	return "Hello user from @PreAuthorize('authenticated') method";
    }

    @PreAuthorize ("hasRole('ROLE_WRITE')")
	public void addEmployee(Employee emp) {}

    @PreAuthorize ("#emp.ename == authentication.name")
    public void deleteEmployee(Employee emp) {}
    
    @PostAuthorize ("returnObject.ename == authentication.name") //el usuario autenticado debe ser 'leo'
	public Employee getEmployee() {
    	Employee emp = new Employee();
    	emp.setEname("leo");
    	return emp;
    }

}
