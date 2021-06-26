package com.sample.mybatis.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sample.mybatis.model.persistence.entity.*;
import com.sample.mybatis.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController//CON RestController NO HACE FALTA @ResponseBody
@RequestMapping("/api/v1/employee")
public class EmployeeRestService {


    //@Autowired
    //public ContactRepository contactRepository;

    @Autowired
    public ICategoryService iCategoryService;
    @Autowired
    public IClientService iClientService;
    @Autowired
    public IContactService iContactService;
    @Autowired
    public ICourierService iCourierService;
    @Autowired
    public IEmployeeService iEmployeeService;
    @Autowired
    public IOrderService iOrderService;
    @Autowired
    public IProductService iProductService;
    @Autowired
    public IProviderService iProviderService;
    @Autowired
    public IAddressService iAddressService;



    //----------------------------------------------------- EMPLOYEES





    @GetMapping( value = "/employees", produces = "application/json")
    public ResponseEntity<?> getAllEmployees(
            HttpServletRequest request,
            HttpServletResponse response) {

        List<Employee> employeeList = iEmployeeService.getAllEmployees();
        //System.out.println(employeeList.get(0));

        if (employeeList.isEmpty()) { return new ResponseEntity(HttpStatus.NO_CONTENT); }

        return new ResponseEntity<Object>(employeeList, HttpStatus.OK);
    }


    @GetMapping(value = "/employees/{id}", produces = "application/json")
    public Employee getEmployeeById(
            @PathVariable(value = "id", required = false) int id) {

        Employee c = iEmployeeService.getByEmployeeId(id);
        return c;
    }

    @PostMapping(value = "/employees")
    public void addEmployees(
            @RequestBody(required = true) Employee employee) {

        iEmployeeService.addEmployee(employee);
    }

    @PutMapping(value = "/employees", produces = "application/json")
    public ResponseEntity<?> updateEmployee(
            @RequestBody(required = true) Employee employee) {

        iEmployeeService.updateEmployee(employee);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping(value = "/employees/{id}", produces = "application/json")
    public ResponseEntity<?> deleteEmployeeById(
            @PathVariable(value = "id", required = false) int id) {

        iEmployeeService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
