/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Otro;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Leo
 */
public class Fregadero {
 
    private Semaphore semaforoF;
    
    public Fregadero(Semaphore s)
    {
        semaforoF = s;
    }
    
    public void lavar()
    {
        try{
            semaforoF.acquire();
        }catch (InterruptedException ex){
            Logger.getLogger(Bandeja.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
    
    public void liberarFregadero()
    {
        semaforoF.release();
    }
}
