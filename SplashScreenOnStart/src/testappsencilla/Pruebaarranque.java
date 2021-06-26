/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testappsencilla;

import javax.swing.JApplet;

/**
 * ESTA CLASE DEMUESTRA QUE DESDE UN APPLET PODEMOS LLAMAR A UN PROGRAMA JAVAFX
 * @author Leo
 */
public class Pruebaarranque extends JApplet {
    
    public void init() {
        Testappsencilla.main(null);
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
