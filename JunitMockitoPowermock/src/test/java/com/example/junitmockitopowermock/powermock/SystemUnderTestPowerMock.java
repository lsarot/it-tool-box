package com.example.junitmockitopowermock.powermock;

import com.example.junitmockitopowermock.mockito.login.UserForm;

public class SystemUnderTestPowerMock {

	public boolean login(UserForm userForm) {
		
		String username = MyStaticMethodsClass.doLogin(userForm);
		
		int i = MyStaticMethodsClass.callMyFinalMethod();

		boolean b = SystemUnderTestPowerMock.callMyStaticMethod(false);
		
		return username.equals("TEST") 
				&& b 
				&& i == 20;
	}
	
	static boolean callMyStaticMethod(boolean b) {
		return b;
	}
	
	public boolean privateMethodCaller() {
		return callPrivateMethod();
	}
	private static boolean callPrivateMethod() {
		return false;
	}
	
	public final String helloMethod() {
        return "Hello World!";
    }
	
}
