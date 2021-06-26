package com.example.springsecdemoJoinRSwithAS.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	/**
	 * Esta es la escencia del RS, exponer endpoints con info del propietario de la cuenta. Claro limitando por scopes, un token emitido por el AS y más.
	 * */
	@GetMapping("/user/me")
	public Principal user(Principal principal) {
		System.out.println(principal);
		return principal;
	}
}
