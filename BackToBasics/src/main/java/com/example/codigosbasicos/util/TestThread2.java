package com.example.codigosbasicos.util;


public class TestThread2 implements Runnable {
    @Override
    public void run() {
        try {
            long antes = System.currentTimeMillis();
            while(System.currentTimeMillis() - antes < 3500) {
                System.out.print("[T's name: " + Thread.currentThread().getName());
                System.out.print("] [T's priority: " + Thread.currentThread().getPriority());
                System.out.print("] [T's state: " + Thread.currentThread().getState().toString());
                System.out.print("] [T's group: " + Thread.currentThread().getThreadGroup().getName());
                System.out.print("] [T is Alive?: " + Thread.currentThread().isAlive());
                System.out.println("] [T is Daemon?: " + Thread.currentThread().isDaemon() + "]");
                
                Thread.sleep(2000);
            }
        } catch(Exception e) { System.out.println("Saliendo a lo bestia! del TestThread2 con este bloque catch."); }
    }
}
