package com.example.codigosbasicos_springboot.topics.getting_properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/test-getproperties")
public class GettingPropertiesController {

	@Autowired
	private PropertiesService propertiesService;
	
	@GetMapping("/m1")
	public ResponseEntity<String> getProperty_1(@RequestParam(value = "key",required = true) String key) {
		String propertyVal = propertiesService.getProperty_1(key);		
		System.out.println(key + " -> " + propertyVal);
		return ResponseEntity.ok(propertyVal);
	}
	
}