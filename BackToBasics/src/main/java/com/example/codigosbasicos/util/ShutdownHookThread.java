package com.example.codigosbasicos.util;

import com.example.codigosbasicos.CodigosBasicos;

public class ShutdownHookThread extends Thread {
    @Override
    public void run() {
        System.out.println(CodigosBasicos.sep+" SHUTDOWN_HOOK_THREAD");
        System.out.println("<<<<<< MENSAJE PROVENIENTE DE UN THREAD SHUTDOWN HOOK COLOCADO AL PRINCIPIO >>>>>>");
    }
}
