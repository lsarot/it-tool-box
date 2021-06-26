/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import Otro.*;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Leo
 */
public class CampamentoSO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Semaphore semaforoB = new Semaphore(0,true);
        Bandeja bandeja = new Bandeja(semaforoB);
        
        Semaphore semaforoS = new Semaphore(3,true);
        Sillas sillas = new Sillas(semaforoS);
        
        Semaphore semaforoF = new Semaphore(2,true);
        Fregadero fregadero = new Fregadero(semaforoF);
       
        for(int i=0; i<10; i++)
        {
            Nino n = new Nino(i+1,bandeja,sillas,fregadero);               
            n.start();
        }
        Cocinero c = new Cocinero(bandeja);
        c.start();
        
    }
}
