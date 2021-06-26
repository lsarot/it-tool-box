package com.example.springwebdemo.configuration.security.httpfilter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;

@Configuration
public class EndpointRequestFilters {

	
	/**
	 * AL REGISTRAR UN BEAN DE TIPO Filter, ESTE SE AGREGA DIRECTAMENTE A LA FILTER CHAIN
	 * 
	 * PERO SI QUEREMOS DARLE UN ORDEN, no lo registramos como Bean y lo colocamos en la configuración de HttpSecurity
	 * 
	 * 
	 * OTRA FORMA DE CREAR FILTRO:
	 * public class JwtAuthenticationFilter extends OncePerRequestFilter {
	 * 		protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
	 * 			...
	 * 			chain.doFilter(req, res);
	 * 		}
	 * }
	 * 
	 * http://localhost:8080/demo-spring-web/get-emp-info-hibernate-ds?token=abc
	 * */
	//@Bean("my-token-filter")
	public static Filter myCustomTokenFilter() {
		return new Filter() {
			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				String token = request.getParameter("token");
				System.out.println("Hi from custom token filter -> My token (must be 'abc') : " + token);
				if (token==null || !token.equals("abc")) {
					//throw an AuthenticationException or AccessDeniedException it will be caught and handled in the ExceptionTranslationFilter.
					//throw new AccessDeniedException("¡Acceso denegado!");
					throw new AccessDeniedWithoutLoggingException("¡Acceso denegado!");
				}
				
				//chain.doFilter(request, response); //SI ES BEAN REGISTRADO SE CONTINÚA LA CADENA, SI NO ES BEAN NO LO HACEMOS PQ QUIEN NOS LLAMÓ LO HARÁ
			}};
	}
	
	//// Workaround to avoid showing full exception stacktrace on log ////
	public static class AccessDeniedWithoutLoggingException extends AccessDeniedException {
		public AccessDeniedWithoutLoggingException(String msg) {super(msg);}
	    @Override public synchronized Throwable fillInStackTrace() {
	        return this;
	    }
	}
	
	
	/**
	 * ESTOS FILTROS AUTOMÁTICAMENTE SE COLOCARÁN AL FINAL DE LA CADENA DE FILTROS
	 * 
	 * arrojar una exception en estos, no mostrará por consola el stacktrace, en el otro caso sí lo está haciendo (cuando le damos un orden)
	 * */
	@Bean("registered-custom-filter")
	public Filter aRegisteredFilter() {
		return new Filter() {
			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				System.out.println("Hi from custom OTHER filter");
				chain.doFilter(request, response);
			}};
	}
	
}
