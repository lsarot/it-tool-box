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
public class Sillas {
    
    private Semaphore semaforoS;
    
    public Sillas(Semaphore s)
    {
        semaforoS = s;
    }
    
    public void sentarseAcomer()
    {
        try{
            semaforoS.acquire();
        }catch(InterruptedException ex){
            Logger.getLogger(Bandeja.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
    
    public void pararse()
    {
        semaforoS.release();
    }
}
