package com.LSO.examples.ws.soap.model;

import java.io.Serializable;

public class BeanLogin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3412201830109317227L;
	private String user = "";
	private String pass = "";
	private String mensaje = "";
	
	public BeanLogin() {
		
	}

	public BeanLogin(String user, String pass) {
		this.user = user;
		this.pass = pass;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
