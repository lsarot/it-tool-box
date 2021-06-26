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
public class Bandeja {
    
    private Semaphore semaforoB;
    private int preparados;
    private int tomados;
    
    public Bandeja(Semaphore s)
    {
        semaforoB = s;
        preparados = 0;
        tomados = 0;
    }
    
    public Semaphore getsemaforo()
    {
        return semaforoB;
    }
    
    public boolean terminado()
    {
        if(preparados == 10)
        {
            return true;
        }else
            return false;
    }
    
    public boolean perroAgarrado()
    {
        if(preparados == tomados)
            return true;
        else
            return false;
    }
    
    public void tomarPerro()
    {
        tomados++;
    }
    
    public void buscarPerro(int identificador)
    {
        try{
            semaforoB.acquire();
        }catch(InterruptedException ex){
            Logger.getLogger(Bandeja.class.getName()).log(Level.SEVERE,null,ex);
        }     
    }
    
    public void ponerPerro()
    {
        semaforoB.release();
        preparados++;
        System.out.println("Preparados hasta el momento: " + preparados);
    }
}
