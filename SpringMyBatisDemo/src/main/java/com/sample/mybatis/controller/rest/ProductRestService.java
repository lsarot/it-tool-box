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
@RequestMapping("/api/v1/product")
public class ProductRestService {


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



    //----------------------------------------------------- PRODUCTS





    @GetMapping( value = "/products", produces = "application/json")
    public ResponseEntity<?> getAllProducts(
            HttpServletRequest request,
            HttpServletResponse response) {

        List<Product> productList = iProductService.getAllProducts();
        //System.out.println(productList.get(0));

        if (productList.isEmpty()) { return new ResponseEntity(HttpStatus.NO_CONTENT); }

        return new ResponseEntity<Object>(productList, HttpStatus.OK);
    }


    @GetMapping(value = "/products/{id}", produces = "application/json")
    public Product getProductById(
            @PathVariable(value = "id", required = false) int id) {

        Product c = iProductService.getByProductId(id);
        return c;
    }

    @PostMapping(value = "/products")
    public void addProduct(
            @RequestBody(required = true) Product product) {

        iProductService.addProduct(product);
    }

    @PutMapping(value = "/products", produces = "application/json")
    public ResponseEntity<?> updateProduct(
            @RequestBody(required = true) Product product) {

        iProductService.updateProduct(product);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping(value = "/products/{id}", produces = "application/json")
    public ResponseEntity<?> deleteProductById(
            @PathVariable(value = "id", required = false) int id) {

        iProductService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
