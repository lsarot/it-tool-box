package com.example.codigosbasicos_springboot.topics.caching.caffeine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CaffeineCacheController {

	@Autowired
    private AddressService addressService;

    @GetMapping("/caching/address/{id}")
    public ResponseEntity<String> getAddress(@PathVariable("id") long customerId) {
        return ResponseEntity.ok(addressService.getAddress(customerId));
    }

    @GetMapping("/caching/address2/{id}")
    public ResponseEntity<String> getAddress2(@PathVariable("id") long customerId) {
        return ResponseEntity.ok(addressService.getAddress2(customerId));
    }
    
    @GetMapping("/caching/address3/{id}")
    public ResponseEntity<String> getAddress3(@PathVariable("id") long customerId) {
        return ResponseEntity.ok(addressService.getAddress3(customerId));
    }
    
    @GetMapping("/caching/address4/{id}")
    public ResponseEntity<String> getAddress4(@PathVariable("id") long customerId) {
        return ResponseEntity.ok(addressService.getAddress4(customerId));
    }
	
}
