package com.example.codigosbasicos.sincronizacion;

public class TestThread extends Thread {
    String name;
    SyncDemo theDemo;
    public TestThread(String name,SyncDemo theDemo) {
        this.theDemo = theDemo;
        this.name = name;
        this.start();
    }
    
    @Override
    public void run() {
        theDemo.test(name);
    }
}
