package com.example.codigosbasicos.wait_notify_notifyall;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TakeThread extends Thread {
    
    private BlockingVector bv;
    
    public TakeThread(BlockingVector bv) {
        this.bv = bv;
    }
    
    @Override
    public void run() {
        try {
            bv.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(TakeThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
