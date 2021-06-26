package com.example.codigosbasicos.arbol;

public class BinaryNode {
    
    private int id;// id del nodo
    private Object mObject;// objeto que contiene el nodo
    private BinaryNode mHijoIzq;// nodo hijo de la izquierda
    private BinaryNode mHijoDer;// nodo hijo de la derecha

    public BinaryNode(int id) { this.id = id; }
    
    public BinaryNode(int id, Object obj) { this.id = id; mObject = obj; }
    
    public int getId() {return id;}
    
    public void setId(int id) { this.id = id; }
    
    public Object getObject() {return mObject;}

    public void setObject(Object obj) { mObject = obj; }

    public BinaryNode getHijoIzq() { return mHijoIzq; }
    
    public void setHijoIzq(BinaryNode n) { mHijoIzq = n; }
   
    public BinaryNode getHijoDer() { return mHijoDer; }
    
    public void setHijoDer(BinaryNode n) { mHijoDer = n; }

    public boolean isLeaf() { return (mHijoIzq == null && mHijoDer == null); }

    public boolean esPadreDeHoja() {
        if (isLeaf()) return false;// chequeamos que el mismo nodo no sea hoja para que no ocurra un nullPointerException, pq no es padre de nadie
        if (mHijoIzq != null && mHijoDer != null) return ( mHijoIzq.isLeaf() || mHijoDer.isLeaf() );
        if (mHijoIzq != null) return mHijoIzq.isLeaf();
        return mHijoDer.isLeaf();// sino es hoja, si sólo alguno de los hijos es dif de null y, no es el izquierdo
    }
}