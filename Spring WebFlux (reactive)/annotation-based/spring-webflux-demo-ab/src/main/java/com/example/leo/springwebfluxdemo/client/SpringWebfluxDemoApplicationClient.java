package com.example.leo.springwebfluxdemo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
/**
  * CHANGE SERVER_PORT TO THE PORT WHERE SERVER WAS INITIALIZED
  * THEN START CLIENT
  * */
@SpringBootApplication
public class SpringWebfluxDemoApplicationClient {

	public static final int SERVER_PORT = 52140;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxDemoApplicationClient.class, args);
		
		
		EmployeeWebClient employeeWebClient = new EmployeeWebClient();
        employeeWebClient.consume();
	}

}
