package com.mongodb.quickstart.initial;

public class ConnectionStringInit {

	public static String CONN_URI;
	
	static {
		System.setProperty("mongodb.psw", "test123mongo");//we should pass it on bootstrapping
		CONN_URI = "mongodb+srv://leo:"+ System.getProperty("mongodb.psw") +"@freecluster-lmrqj.mongodb.net/test?retryWrites=true&w=majority";
	}
	
}
