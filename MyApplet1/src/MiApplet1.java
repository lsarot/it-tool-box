
package miapplet1;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MiApplet1 extends JApplet {
    
    public JFrame f;
    
    public MiApplet1() {
        f = new JFrame("Test Applet/Aplicación");
        
        JApplet instancia = this;
        
        //añadir la instancia del applet al marco 
        f.getContentPane().setLayout(new BorderLayout()); 
        f.getContentPane().add("Center", instancia); 

        //inicializar las variables al ancho y el alto para cuando abro como app de escritorio
        f.setSize(300, 150);

        //con esto cierro el thread al cerrar la ventana, sino queda activo
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                f.dispose();
            }
        });
        
        //añado un label al frame
        JLabel lb = new JLabel("HOLA");
        f.getContentPane().add("Center", lb);
        
        //llamar a init() y a start()
        init(); 
        start();

        //hacer visible el marco 
        f.setVisible(true);
    }
    
    public static void main(String[] args) {
        new MiApplet1(); 
    }
    
    public void init() {
        
    }
    
    public void start() {
        
    }
    
    public void paint() {
        
    }
    
    public void stop() { 
        
    }
    
    @Override
    public void destroy() { 
        this.destroy();
    }
}
