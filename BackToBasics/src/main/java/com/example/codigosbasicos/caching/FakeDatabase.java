package com.example.codigosbasicos.caching;

import java.util.HashMap;
import java.util.Map;

public class FakeDatabase {
	
	private FakeDatabase() {}
	
	static {
		database = new HashMap<>();
		populateDB();
	}

	private static Map<String, Employee> database;

	private static void populateDB() {
		//first we populate out fake database (a key-value pair Map)
		Employee e1 = new Employee("Mahesh", "Finance", "100");
		Employee e2 = new Employee("Abigail", "HHRR", "101");
		Employee e3 = new Employee("John", "Security", "102");
		Employee e4 = new Employee("Rohan", "IT", "103");
		Employee e5 = new Employee("Sohan", "Admin", "110");
		database.put("100", e1);
		database.put("101", e2);
		database.put("102", e3);
		database.put("103", e4);
		database.put("110", e5);
	}
	
	public static Employee getFromDatabase(String empId) {
		try {java.util.concurrent.TimeUnit.MILLISECONDS.sleep(500);} catch (InterruptedException e) {System.out.println(e.getLocalizedMessage());}
		System.out.println("Database hit for " + empId);
		return database.get(empId);
	}
	
}
