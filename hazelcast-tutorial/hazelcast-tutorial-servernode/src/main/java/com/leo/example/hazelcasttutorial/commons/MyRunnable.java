package com.leo.example.hazelcasttutorial.commons;

import java.io.Serializable;
import java.time.Instant;

public class MyRunnable implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;
	private int i;
	private long ts;
	
	public MyRunnable(int i, long ts) {
		this.i = i;
		this.ts = ts;
	}
	
	@Override
	public void run() {
		try {Thread.sleep(100);} catch(Exception e) {}
    	System.out.println(i+" " + (Instant.now().toEpochMilli() - ts));
	}
}
