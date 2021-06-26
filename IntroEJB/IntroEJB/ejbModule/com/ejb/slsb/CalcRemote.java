package com.ejb.slsb;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.sun.corba.se.impl.orbutil.closure.Future;

@Remote
public interface CalcRemote {
	public int addition(int a, int b);
	public void asincronoSinRspta();
}
