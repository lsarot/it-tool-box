package com.example.junitmockitopowermock.mockito.login;

public class LoginController {
	
    private LoginService loginService;
 
    public String login(UserForm userForm) {
        if(null == userForm) {
            return "ERROR";
        } else {
            boolean logged;
 
            try {
                logged = loginService.login(userForm);
            } catch (Exception e) {
                return "ERROR";
            }
 
            if(logged) {
                loginService.setCurrentUser(userForm.getUsername());
                return "OK";
            } else {
                return "KO";
            }
        }
    }
    
    public void setLoginService(LoginService ls) {
    	this.loginService = ls;
    }
    
}