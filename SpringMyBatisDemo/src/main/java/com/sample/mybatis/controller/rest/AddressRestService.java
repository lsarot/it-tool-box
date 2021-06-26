package com.sample.mybatis.controller.rest;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sample.mybatis.model.persistence.entity.*;
import com.sample.mybatis.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Para configurar el Swagger no es necesario las anotaciones en los Rest endpoints
 * 		Aquí lo usamos para mostrar en detalle la configuración
 * */


@Api(value = "address", description = "Address API", produces = "application/json") //SWAGGER
@RestController //CON RestController NO HACE FALTA @ResponseBody
@RequestMapping("/api/v1/address")
public class AddressRestService {


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


    //----------------------------------------------------- ADDRESSES


    @ApiOperation(value = "Get Addresses", notes = "returns all addresses") //SWAGGER
    @ApiResponses({ //SWAGGER
            @ApiResponse(code = 200, message = "returns one address at least")
    })
    @GetMapping(value = "/addresses", produces = "application/json")
    public ResponseEntity<?> getAllAddresses(
            HttpServletRequest request, HttpServletResponse response) {

        List<Address> addressList = iAddressService.getAllAddresses();
        //System.out.println(addressList.get(0));

        if (addressList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }


    @ApiOperation(value = "Get one Address", notes = "returns one address")
    @ApiResponses({
            @ApiResponse(code = 200, message = "returns this address")
    })
    @ApiParam(defaultValue = "1",value = "The id of the address to return") //SWAGGER
    @GetMapping(value = "/addresses/{id}", produces = "application/json")
    public Address getAddressById(
            @PathVariable(value = "id", required = false) int id) {

        Address c = iAddressService.getByAddressId(id);
        return c;
    }

    
    @PostMapping(value = "/addresses")
    public void addAddress(
            @RequestBody(required = true) Address address) {

        iAddressService.addAddress(address);
    }

    
    @PutMapping(value = "/addresses", produces = "application/json")
    public ResponseEntity<?> updateAddress(
            @RequestBody(required = true) Address address) {

        iAddressService.updateAddress(address);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping(value = "/addresses/{id}", produces = "application/json")
    public ResponseEntity<?> deleteAddressById(
            @PathVariable(value = "id", required = false) int id) {

        iAddressService.deleteAddress(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}