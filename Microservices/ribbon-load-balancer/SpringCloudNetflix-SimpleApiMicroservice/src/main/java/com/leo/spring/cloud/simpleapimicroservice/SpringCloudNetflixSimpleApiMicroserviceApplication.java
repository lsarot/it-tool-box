package com.leo.spring.cloud.simpleapimicroservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class SpringCloudNetflixSimpleApiMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudNetflixSimpleApiMicroserviceApplication.class, args);
	}
	
	
	@Autowired Environment env;
	
	@GetMapping("/test")
	public String testEndpoint() {
		return String.format("Service port is: %s", env.getProperty("server.port"));
	}

}
