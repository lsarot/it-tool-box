package com.example.junitmockitopowermock.mockito.login;

public class UserForm {
	
    public String password;
    public String username;
    
    public UserForm() {}
    
    public UserForm(String password, String username) {
		this.password = password;
		this.username = username;
	}

	public String getUsername() {
        return username;
    }
}
