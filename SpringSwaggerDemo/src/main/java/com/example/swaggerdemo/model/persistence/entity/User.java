package com.example.swaggerdemo.model.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.*;

@Entity
public class User {
    
	@Id
    private Long id;
	
	@NotNull(message = "First Name cannot be null") //JavaValidation JSR380
    private String firstName;
	
	@Min(value = 15, message = "Age should not be less than 15") //JavaValidation JSR380
    @Max(value = 65, message = "Age should not be greater than 65")
    private int age;
	
	@Email(regexp=".@.\\..*", message = "Email should be valid")
    private String email;
    
	private String password;
	
    
	public Long getId() {
		return id;
	}
	public String getFirstName() {
		return firstName;
	}
	public int getAge() {
		return age;
	}
	public String getEmail() {
		return email;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
    
}
