package com.example.springwebdemo.model.persistence.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.springwebdemo.model.persistence.dao.EmployeeDao;
import com.example.springwebdemo.model.persistence.entity.Employee;

/**
 * NOTAR EL USO DE @Repository COMO UNA CLASE.
 * 
 * EN EL PROYECTO SE PUEDE USAR @Repository SOBRE UNA INTERFACE 
 * QUE EXTIENDA DE CrudRepository<Entidad, PKtype> O PagingAndSortingRepository
 * pero estas limitan a usar ese único tipo para devolver en cada método, ya que la query SQL debe devolver un tipo de clase Entidad
 * pq mapea el resultset a ese tipo.
 * 
 * SOBRE INTERFACE SERÍAN MÁS BIEN COMO DAOS DE LA ENTIDAD PARTICULAR, 
 * LO HAGO SOBRE CLASE PARA USAR VARIOS DAOS Y HACER QUERIES MÁS COMPLEJAS QUE MAPEEN BAJO MI CRITERIO A UN DOMAIN OBJECT O UN DTO.
 * */

@Repository
		//// TRANSACTIONS: when using Spring Data, calls on Spring Data repositories are by default surrounded by a transaction, even without @EnableTransactionManagement (in @Configuration class) . If Spring Data finds an existing transaction, the existing transaction will be re-used, otherwise a new transaction is created.
		//// @Transactional annotations within your own code, however, are only evaluated when you have @EnableTransactionManagement activated (or configured transaction handling some other way).
		//// You can easily trace transaction behavior by adding the following property to your application.properties:    logging.level.org.springframework.transaction.interceptor=TRACE
public class EmployeeRepo {
	
	@Autowired private EmployeeDao employeeDao;
	

	public List<Employee> fetchAll_using_h2_1() {
		return employeeDao.fetchAll_using_h2_1();
	}
	
	
	public List<Employee> fetchAll_using_jndi_datasource() {
		return employeeDao.fetchAll_using_jndi_datasource();
	}
	
	
	public List<Employee> findAll_using_hibernate() {
		return employeeDao.findAll_using_hibernate_HQL();
	}


	public void mod_employee_info() {
		employeeDao.mod_employee_info();
	}
	
}
