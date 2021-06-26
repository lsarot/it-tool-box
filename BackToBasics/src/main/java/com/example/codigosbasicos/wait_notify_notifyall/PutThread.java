package com.example.codigosbasicos.wait_notify_notifyall;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PutThread extends Thread {
    
    private BlockingVector bv;
    private int i;
    
    public PutThread(BlockingVector bv, int i) {
        this.bv = bv;
        this.i = i;
    }
    
    @Override
    public void run() {
        try {
            bv.put(i);
        } catch (InterruptedException ex) {
            System.out.println("- Thread interrumpido -");
            Logger.getLogger(PutThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
