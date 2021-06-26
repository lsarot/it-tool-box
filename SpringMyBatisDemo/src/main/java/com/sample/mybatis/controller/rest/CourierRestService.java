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
@RequestMapping("/api/v1/courier")
public class CourierRestService {


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



    //----------------------------------------------------- COURIERS





    @GetMapping( value = "/couriers", produces = "application/json")
    public ResponseEntity<?> getAllCouriers(
            HttpServletRequest request,
            HttpServletResponse response) {

        List<Courier> courierList = iCourierService.getAllCouriers();
        //System.out.println(courierList.get(0));

        if (courierList.isEmpty()) { return new ResponseEntity(HttpStatus.NO_CONTENT); }

        return new ResponseEntity<Object>(courierList, HttpStatus.OK);
    }


    @GetMapping(value = "/couriers/{id}", produces = "application/json")
    public ResponseEntity<?> getCourierById(
            @PathVariable(value = "id", required = false) int id) {

        Courier c = iCourierService.getByCourierId(id);
        return new ResponseEntity<Object>(c, HttpStatus.OK);
    }

    @PostMapping(value = "/couriers")
    public void addCourier(
            @RequestBody(required = true) Courier courier) {

        iCourierService.addCourier(courier);
    }

    @PutMapping(value = "/couriers", produces = "application/json")
    public ResponseEntity<?> updateCourier(
            @RequestBody(required = true) Courier courier) {

        iCourierService.updateCourier(courier);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping(value = "/couriers/{id}", produces = "application/json")
    public ResponseEntity<?> deleteCourierById(
            @PathVariable(value = "id", required = false) int id) {

        iCourierService.deleteCourier(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
