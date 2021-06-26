/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Otro;

/**
 *
 * @author Leo
 */
public class Nino extends Thread{
    
    private int identificador;
    private Bandeja bandeja;
    private Sillas sillas;
    private Fregadero fregadero;
    
    public Nino(int id, Bandeja b, Sillas s, Fregadero f)
    {
        this.identificador = id;
        this.bandeja = b;
        this.sillas = s;
        this.fregadero = f;
    }
    
    private void esperaBuscarP()//tarda aprox de 5 a 10s en buscar su perro
    {
        try{
            sleep(5000 + (int)(10000*Math.random()));
        }catch(Exception e){           
        }
    }
    
    private void esperaSentarse()//tarda aprox de 5 a 10s en sentarse
    {
        try{
            sleep(5000 + (int)(10000*Math.random()));
        }catch(Exception e){           
        }
    }
    
    private void esperaLavar()//tarda aprox de 5 a 10s en ir a lavar
    {
        try{
            sleep(5000 + (int)(10000*Math.random()));
        }catch(Exception e){           
        }
    }
    
    private void esperaComiendo()//tarda aprox 30 a 60s en comer
    {
        try{
            sleep(30000 + (int)(30000*Math.random()));
        }catch(Exception e){           
        }
    }
    
    private void esperaLavando()//tarda aprox de 10 a 15s lavando
    {
        try{
            sleep(10000 + (int)(15000*Math.random()));
        }catch(Exception e){           
        }
    }
    
    @Override
    public void run()
    {   
        bandeja.buscarPerro(identificador);
        System.out.println("Niño " + identificador + " yendo a buscar perro caliente.");
        esperaBuscarP();
        System.out.println("Niño " + identificador + " llegó a donde el perrero.");
        bandeja.tomarPerro();
        sillas.sentarseAcomer();
        System.out.println("Niño " + identificador + " vá a sentarse.");
        esperaSentarse();
        System.out.println("Niño " + identificador + " está sentado y empieza a comer.");
        esperaComiendo();
        System.out.println("Niño " + identificador + " terminó de comer.");
        sillas.pararse();
        fregadero.lavar();
        System.out.println("Niño " + identificador + " yendo a lavar su plato.");
        esperaLavar();
        System.out.println("Niño " + identificador + " tomó un fregadero.");
        esperaLavando();
        System.out.println("Niño " + identificador + " terminó de lavar.");
        fregadero.liberarFregadero();
    }
}
