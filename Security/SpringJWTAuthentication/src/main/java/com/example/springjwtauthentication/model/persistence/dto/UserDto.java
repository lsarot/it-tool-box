package com.example.springjwtauthentication.model.persistence.dto;


public class UserDto {

    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private long salary;
    private int age;
    
	public long getId() {
		return id;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public long getSalary() {
		return salary;
	}
	public int getAge() {
		return age;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setSalary(long salary) {
		this.salary = salary;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}
