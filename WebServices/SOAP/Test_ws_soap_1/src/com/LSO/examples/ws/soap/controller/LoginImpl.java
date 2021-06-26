package com.LSO.examples.ws.soap.controller;

import com.LSO.examples.ws.soap.model.BeanLogin;

public class LoginImpl implements Login {

	@Override
	public BeanLogin validarLogin(BeanLogin b) {
		BeanLogin response = new BeanLogin();
		if(b.getUser().equals("root") && b.getPass().equals("0000")) {
			response.setMensaje("Acceso concedido!");
		} else {
			response.setMensaje("Acceso denegado!");
		}
		return response;
	}

}
