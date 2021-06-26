package com.example.springjwtauthentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springjwtauthentication.configuration.security.JwtTokenUtil;
import com.example.springjwtauthentication.model.persistence.dto.AuthToken;
import com.example.springjwtauthentication.model.persistence.dto.LoginUser;

/**
 * to create token on user behalf
 * */

@RestController
@RequestMapping("/token")
public class AuthenticationController {

	//@Autowired private UserService userService;

	@Autowired private AuthenticationManager authenticationManager;

    @Autowired private JwtTokenUtil jwtTokenUtil;


    @PostMapping(value = "/generate-token")
    public ResponseEntity register(@RequestBody LoginUser loginUser) throws AuthenticationException {

    	/* TENEMOS 3 METODOS CLAVES del contexto de seguridad Spring:
    	 * Revisar si es válido el usuario: authenticationManager.authenticate
    	 * Setear usuario en sec context: SecurityContextHolder.getContext().setAuthentication(authentication)
    	 * Recuperar usuario autenticado: SecurityContextHolder.getContext().getAuthentication()
    	 * */
    	
    	/* Con credenciales malas genera un BadCredentialsException
			Esta exception es más costosa que revisar en bbdd las credenciales
			mejor revisar y generar si están OK :)
    	 * */
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword())
        );
        
        //tomamos la entidad de la bbdd pq querremos tener más datos para generar el token.. rol por ejemplo
        //SI USAMOS NUESTRO PROPIO SCHEMA
        //final User user = userService.findByUsername(loginUser.getUsername());
        //final String token = jwtTokenUtil.generateToken(user);
        //SI USAMOS EL DEFAULT SCHEMA DE SPRING (Users, Authorities)
			        //org.springframework.security.core.userdetails.User
			        //org.springframework.security.core.authority.SimpleGrantedAuthority
        final String token = jwtTokenUtil.generateToken(authentication);
        return ResponseEntity.ok(new AuthToken(token));
    }
    

}