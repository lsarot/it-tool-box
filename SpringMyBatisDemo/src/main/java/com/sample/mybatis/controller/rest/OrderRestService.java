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
@RequestMapping("/api/v1/order")
public class OrderRestService {


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



    //----------------------------------------------------- ORDERS





    @GetMapping( value = "/orders", produces = "application/json")
    public ResponseEntity<?> getAllOrders(
            HttpServletRequest request,
            HttpServletResponse response) {

        List<Order> orderList = iOrderService.getAllOrders();
        //System.out.println(orderList.get(0));

        if (orderList.isEmpty()) { return new ResponseEntity(HttpStatus.NO_CONTENT); }

        return new ResponseEntity<Object>(orderList, HttpStatus.OK);
    }


    @GetMapping(value = "/orders/{id}", produces = "application/json")
    public ResponseEntity<?> getOrderById(
            @PathVariable(value = "id", required = false) int id) {

        Order c = iOrderService.getByOrderId(id);
        return new ResponseEntity<Object>(c, HttpStatus.OK);
    }

    @PostMapping(value = "/orders")
    public void addOrder(
            @RequestBody(required = true) Order order) {

        iOrderService.addOrder(order);
    }

    @PutMapping(value = "/orders", produces = "application/json")
    public ResponseEntity<?> updateOrder(
            @RequestBody(required = true) Order order) {

        iOrderService.updateOrder(order);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping(value = "/orders/{id}", produces = "application/json")
    public ResponseEntity<?> deleteOrderById(
            @PathVariable(value = "id", required = false) int id) {

        iOrderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
