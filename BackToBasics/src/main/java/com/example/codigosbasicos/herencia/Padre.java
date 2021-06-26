/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.codigosbasicos.herencia;

/**
 *
 * @author Leo
 */
public abstract class Padre {
    
    public int idPadre;
    
    public Padre(int id){
        this.idPadre = id;
        System.out.println("ConstructorPadre");
    }
    
    public void metodoPadre1() {
        System.out.println("MetodoPadre1");
    }
    
    public void metodoPadre2() {
        System.out.println("MetodoPadre2");
    }
    
    public abstract void metodoAbstractPadre();
}
