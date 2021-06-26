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
@RequestMapping("/api/v1/provider")
public class ProviderRestService {


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


    //----------------------------------------------------- PROVIDERS





    @GetMapping( value = "/providers", produces = "application/json")
    public ResponseEntity<?> getAllProviders(
            HttpServletRequest request,
            HttpServletResponse response) {

        List<Provider> providerList = iProviderService.getAllProviders();
        //System.out.println(providerList.get(0));

        if (providerList.isEmpty()) { return new ResponseEntity(HttpStatus.NO_CONTENT); }

        return new ResponseEntity<Object>(providerList, HttpStatus.OK);
    }


    @GetMapping(value = "/providers/{id}", produces = "application/json")
    public Provider getProviderById(
            @PathVariable(value = "id", required = false) int id) {

        Provider c = iProviderService.getByProviderId(id);
        return c;
    }

    @PostMapping(value = "/providers")
    public void addProvider(
            @RequestBody(required = true) Provider provider) {

        iProviderService.addProvider(provider);
    }

    @PutMapping(value = "/providers", produces = "application/json")
    public ResponseEntity<?> updateProvider(
            @RequestBody(required = true) Provider provider) {

        iProviderService.updateProvider(provider);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping(value = "/providers/{id}", produces = "application/json")
    public ResponseEntity<?> deleteProviderById(
            @PathVariable(value = "id", required = false) int id) {

        iProviderService.deleteProvider(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
