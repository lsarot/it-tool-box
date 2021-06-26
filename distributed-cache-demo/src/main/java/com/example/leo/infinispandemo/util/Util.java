package com.example.leo.infinispandemo.util;

import java.util.function.Supplier;

public class Util {
	
	/** Just to simulate some heavy query
	 * */
	public static String getFromDatabase(String key) {
        try {
            System.out.println("Executing some heavy query");
            Thread.sleep(1000);
        } catch (InterruptedException e) {e.printStackTrace();}
        return "Value for " + key;
    }
	
	
	/** To test execution time
	 * */
	public static <T> long timeThis(Supplier<T> supplier) {
	    long millis = System.currentTimeMillis();
	    supplier.get();
	    return System.currentTimeMillis() - millis;
	}
	
}
