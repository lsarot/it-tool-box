package com.example.codigosbasicos.sockets;

import java.io.Serializable;

public class ObjetoSerializable implements Serializable {
    
    public static final long serialVersionUID = 1L;
    
    private int int1;
    private String cadena;
    private byte b;
    private ObjetoSerializable hijo;
    
    public ObjetoSerializable() {}
    
    public ObjetoSerializable(int i, String cad, byte b, ObjetoSerializable obs) {
        int1 = i;
        cadena = cad;
        this.b = b;
        hijo = obs;
    }
    
    @Override
    public String toString() {
        return "[int: " + int1 + "] [String: " + cadena + "] [byte: " + b + "] ::: [hijo: " + hijo + "]";
    }
}