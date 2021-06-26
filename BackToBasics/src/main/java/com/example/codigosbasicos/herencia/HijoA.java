/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.codigosbasicos.herencia;

/**
 *
 * @author Leo
 */
public class HijoA extends Padre {

    public int idHijoA;
    
    public HijoA(int id) {
        super(10);
        this.idHijoA = id;
    }
    
    @Override
    public void metodoAbstractPadre() {
        System.out.println("MetodoAbstractPadre En hijoA");
    }
    
    public void metodoHijoA() {
        System.out.println("MetodoHijoA");
    }
    
    @Override
    public void metodoPadre2(){
        System.out.println("MetodoPadre2 En HijoA");
    }
}
