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
@RequestMapping("/api/v1/client")
public class ClientRestService {


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




    //----------------------------------------------------- CLIENTS






    @GetMapping( value = "/clients", produces = "application/json")
    public ResponseEntity<?> getAllClients(
            HttpServletRequest request,
            HttpServletResponse response) {

        List<Client> clientList = iClientService.getAllClients();
        //System.out.println(clientList.get(0));

        if (clientList.isEmpty()) { return new ResponseEntity(HttpStatus.NO_CONTENT); }

        return new ResponseEntity<Object>(clientList, HttpStatus.OK);
    }


    @GetMapping(value = "/clients/{id}", produces = "application/json")
    public Client getClientById(
            @PathVariable(value = "id", required = false) int id) {

        Client c = iClientService.getByClientId(id);
        return c;
    }

    @PostMapping(value = "/clients")
    public void addClient(
            @RequestBody(required = true) Client client) {

        iClientService.addClient(client);
    }

    @PutMapping(value = "/clients", produces = "application/json")
    public ResponseEntity<?> updateClient(
            @RequestBody(required = true) Client client) {

        iClientService.updateClient(client);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping(value = "/clients/{id}", produces = "application/json")
    public ResponseEntity<?> deleteClientById(
            @PathVariable(value = "id", required = false) int id) {

        iClientService.deleteClient(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
