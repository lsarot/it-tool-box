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
@RequestMapping("/api/v1/category")
public class CategoryRestService {


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



    //----------------------------------------------------- CATEGORIES





    @GetMapping( value = "/categories", produces = "application/json")
    public ResponseEntity<?> getAllCategories(
            HttpServletRequest request,
            HttpServletResponse response) {

        List<Category> categoryList = iCategoryService.getAllCategories();
        //System.out.println(categoryList.get(0));

        if (categoryList.isEmpty()) { return new ResponseEntity(HttpStatus.NO_CONTENT); }

        return new ResponseEntity<Object>(categoryList, HttpStatus.OK);
    }


    @GetMapping(value = "/categories/{id}", produces = "application/json")
    public Category getCategoryById(
            @PathVariable(value = "id", required = false) String id) {

        Category c = iCategoryService.getByCategoryId(id);
        return c;
    }

    @PostMapping(value = "/categories")
    public void addCategory(
            @RequestBody(required = true) Category category) {

        iCategoryService.addCategory(category);
    }

    @PutMapping(value = "/categories", produces = "application/json")
    public ResponseEntity<?> updateCategory(
            @RequestBody(required = true) Category category) {

        iCategoryService.updateCategory(category);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping(value = "/categories/{id}", produces = "application/json")
    public ResponseEntity<?> deleteCategoryById(
            @PathVariable(value = "id", required = false) String id) {

        iCategoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
