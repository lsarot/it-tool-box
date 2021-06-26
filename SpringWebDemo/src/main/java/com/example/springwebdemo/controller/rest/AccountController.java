package com.example.springwebdemo.controller.rest;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SÓLO PARA REVISAR LA CUENTA QUE CREAMOS EN LA BBDD
 * */

@RestController
@RequestMapping("/principal")
public class AccountController {

	/**
	 * parece ridículo este endpoint, pero lo usamos para probar que tuvimos acceso con la configuración de seguridad impuesta.
	 * */
	@GetMapping
	public Principal retrievePrincipal(Principal principal) {
		return principal;
	}
}
