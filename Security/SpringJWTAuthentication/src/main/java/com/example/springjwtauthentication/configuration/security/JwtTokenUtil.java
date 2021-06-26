package com.example.springjwtauthentication.configuration.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.example.springjwtauthentication.util.Constants.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.springjwtauthentication.model.persistence.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

/**
 * Following is the util class to generate the auth token as well as to extract username from the token.
 * Here is the configuration that we want url such as /token/* and /signup/* to be publicly available and rest of the urls to be restricted from public access.
 * */

@Component
public class JwtTokenUtil {

    public String getUsernameFromToken(String token) throws SignatureException, ExpiredJwtException {
        return (String) getClaimFromToken(token, "sub");
    }

    public Date getExpirationDateFromToken(String token) {
        return new Date(1000L * (Integer)getClaimFromToken(token, "exp"));
    }

    public Object getClaimFromToken(String token, String claim) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get(claim);
    }
    
    /**
     * Utility for not checking each time for the signature in method getAllClaimsFromToken
     * 
     * Llamamos primero a getAllClaimsFromToken, luego a este y le pasamos Claims
     * Los 3 de arriba no son necesarios!
     * */
    public Object getClaimFromClaims(Claims claims, String claim, Object defaultVal) {
    	return claims.getOrDefault(claim, defaultVal);
    }
    
    /**
     * ESTE MÉTODO YA VALIDA INTEGRIDAD DEL HEADER Y PAYLOAD,  Y CADUCIDAD DEL TOKEN
     * */
    public Claims getAllClaimsFromToken(String claimsJws) {
		return Jwts.parser()
				.setSigningKey(SIGNING_KEY)
				.parseClaimsJws(claimsJws)
				//.getHeader()
				//.getSignature()
				.getBody();
    }
    
    /**
     * debería usarse el de abajo, donde se le pasa un ente ya Authenticated
     * este es si nosotros mismos buscamos en la bbdd pq no nos ajustamos al esquema de bbdd de la capa spring security, o configuramos para que recupere de nuestro esquema.
     * */
    public String generateToken(User user) {
        return doGenerateToken(
        		user.getUsername(),
        		Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
    
    /**
     * favorite method to generate token
     * */
    public String generateToken(Authentication auth) {
    	org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)auth.getPrincipal(); 
    	return doGenerateToken(
    			user.getUsername(),
    			Arrays.asList(user.getAuthorities()));
    }

    private String doGenerateToken(String username, List authorities) {
    	//payload
        Claims claims = Jwts.claims()
        		.setSubject(username);
        		//.setExpiration(exp);
        		//.set...
        claims.put("scopes", authorities);
        claims.put("name", username);

        //build JWT
        return Jwts.builder()
        		.setHeaderParam("typ", "JWT")
                .setClaims(claims) //setear el payload a partir de un Claims
                //.setPayload("") //setear payload a partir de un string (no usar ambos)
                //.setHeader... //varios métodos para setear header
                //podemos añadir claims también por esta vía
                .setIssuer("http://leonacho.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();//A compact URL-safe JWT string
    }

    
    /**
     * ya se valida el token cuando se llama a getAllClaimsFromToken(String token)
     * */
    /*public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (
              username.equals(userDetails.getUsername())
                    && !isTokenExpired(token));
    }
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }*/
    
}
