package com.example.junitmockitopowermock.powermock;

import com.example.junitmockitopowermock.mockito.login.UserForm;

public class MyStaticMethodsClass {
	
	public static String doLogin(UserForm uf) {
		return uf.getUsername();
	}
	
	public final static int callMyFinalMethod() {
		return 10;
	}
	
}
