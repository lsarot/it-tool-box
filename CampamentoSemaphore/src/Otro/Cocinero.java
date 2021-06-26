/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Otro;

/**
 *
 * @author Leo
 */
public class Cocinero extends Thread{
    
    private Bandeja bandeja;
    
    public Cocinero(Bandeja b)
    {
        this.bandeja = b;
    }
    
    private void esperar()//tarda aprox de 25 a 30s preparando un perro
    {
        try{
            Thread.sleep((long) (25000 + 5000*Math.random()));
        }catch(Exception e){}
    }
    
    @Override
    public void run()
    {
        while(bandeja.terminado() == false)
        {
            if((bandeja.getsemaforo().availablePermits()==0) && (bandeja.perroAgarrado()==true))
            {
                System.out.println("Cocinero puede preparar perro caliente porque la bandeja está vacía.");
                esperar();
                System.out.println("Cocinero terminó de preparar perro caliente.");
                bandeja.ponerPerro();
            }
                      
        }
        System.out.println("Cocinero terminó los 10 perro calientes.");
    }
}
