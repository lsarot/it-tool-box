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
@RequestMapping("/api/v1/contact")
public class ContactRestService {


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


    //----------------------------------------------------- CONTACTS


    @RequestMapping(method = RequestMethod.GET, value = "/contacts", produces = "application/json")
    public ResponseEntity<?> getAllContacts(
                                HttpServletRequest request,
                                HttpServletResponse response) {

        List<Contact> contactList = iContactService.getAllContacts();
        //System.out.println(contactList.get(0));

        if (contactList.isEmpty()) { return new ResponseEntity(HttpStatus.NO_CONTENT); }

        return new ResponseEntity<Object>(contactList, HttpStatus.OK);
    }


    @GetMapping(value = "/contacts/{id}", produces = "application/json")
    public Contact getContactById(
            @PathVariable(value = "id", required = false) int id) {

        Contact c = iContactService.getByContactId(id);
        return c;
    }

    
    @PostMapping(value = "/contacts")
    public void addContact(
            @RequestBody(required = true) Contact contact) {

        iContactService.addContact(contact);
    }
    

    @PutMapping(value = "/contacts", produces = "application/json")
    public ResponseEntity<?> updateContact(
            @RequestBody(required = true) Contact contact) {

        iContactService.updateContact(contact);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping(value = "/contacts/{id}", produces = "application/json")
    public ResponseEntity<?> deleteContactById(
            @PathVariable(value = "id", required = false) int id) {

        iContactService.deleteContact(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
