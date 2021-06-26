package com.ejb.slsb;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import com.sun.corba.se.impl.orbutil.closure.Future;

/**
 * Session Bean implementation class Calc
 */
@Remote
@Stateless
public class Calc implements CalcRemote {

	@Resource
	SessionContext sessionContext;
	
    /**
     * Default constructor. 
     */
    public Calc() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public int addition(int a, int b) {
		return a + b;
	}

	@Override
	@Asynchronous
	public void asincronoSinRspta() {//LANZA UNA EXCEPTION AL TRATAR DE EJECUTAR UN MÉTODO ASINCRONO CON WILDFLY11
	    if (sessionContext.wasCancelCalled()) {
	        // clean up
	    } else {
	        System.out.println("Ejecutando método async sin respuesta!");
	    }
	}

}
