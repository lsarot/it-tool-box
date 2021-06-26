package com.example.springjwtauthentication.configuration.security.httpfilter;

import static com.example.springjwtauthentication.util.Constants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.springjwtauthentication.configuration.security.JwtTokenUtil;
import com.example.springjwtauthentication.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

/**
 * This class checks for the Authorization header and authenticates the JWT token and sets the authentication in the context
 * */

public class JwtAuthenticationFilter extends OncePerRequestFilter { //ensures a single execution per request dispatch

	//se necesitaba con la versión antigua. Cuando UserService implementaba la interface UserDetailsService.
    //@Autowired private UserDetailsService userDetailsService;
	
	@Autowired private UserService userService;

    @Autowired private JwtTokenUtil jwtTokenUtil;

    
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        
    	String header = req.getHeader(HEADER_STRING);
        String sub = null;
        String authToken = null;
        Claims claims = null;
        ArrayList<ArrayList<LinkedHashMap<String, String>>> scopes = null;
        
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX, "");
            try {
            	claims = jwtTokenUtil.getAllClaimsFromToken(authToken);
            	sub = (String) jwtTokenUtil.getClaimFromClaims(claims, "sub", null);//usamos sub pq es un claim predefinido en el standard
            	scopes = (ArrayList<ArrayList<LinkedHashMap<String, String>>>) jwtTokenUtil.getClaimFromClaims(claims, "scopes", null);
            } catch (IllegalArgumentException e) {
                logger.error("an error occured during getting username from token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("the token is expired and not valid anymore", e);
            } catch(SignatureException e){
                logger.error("Authentication Failed.");//la arroja también cuando cambio payload, aunque mantenga una signature que sí sea OK
            }
        } else {
            logger.warn("couldn't find bearer string, will ignore the header");
        }
        
        /** ONCE HERE:
    	 * think we could be working or not with Spring Security layer, and protecting our endpoints with a filter (this case), or maybe this filter could be placed directly in the httpServlet flow (not with Spring Security). So,
    	 * IF WE AREN'T USING SPRING SEC:
    	 * continue the filter chain or just throw an exception. (bad idea, exceptions generate more stacktrace)
    	 * IF WE ARE USING SPRING SEC AND HAVE CONFIGURED PATHS PROTECTION:
    	 * continue the filter chain, but before, do register or not an authentication in the security context, the security layer will take care of the rest, allowing or not to access resources based on the roles for example.
    	 * like we do now: 
    	 * */
        
        //si encontró username, y no hay un ente autenticado en el contexto (no debería pq es threadlocal strategy)        
        if (sub != null && scopes != null && SecurityContextHolder.getContext().getAuthentication() == null) { //getContext().etAuthentication() debiera ser null pq es para el current thread, y usamos un thread per request.
        	
        	//llegado a este punto el token está integro, podemos confiar en sus claims
        	//igualmente el autor del tutorial decidió buscar usuario en la bbdd y genera un UserDetails
        	//esto sirve es si usamos un schema propio, si usamos el de Spring no servirá, pq se guarda en otra tabla (notar que busca del userService que usa el userDao)
        	/**UserDetails userDetails = userService.loadUserByUsername(username);
        	//valida que el token coincide con usuario en bbdd.. (no confía en la esencia de JWT ?, de que los datos están íntegros, de que el JWS viene firmado por una key nuestra)
            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
            	//va a registrar un ente con rol ADMIN en el contexto.. pero esto debería ya estar guardado en el schema de bbdd y tomarlo de ahí
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authentication);//esta línea es clave para habilitar a lo que pueda acceder dicho usuario
            }*/
        	
        	//LA IDEA ES NO USAR authenticationManager.authenticate (busca en bbdd o memoria según hayamos configurado en HttpSecurityConfig >> configure(AuthenticationManagerBuilder auth))
        	//ni buscar en la bbdd nosotros, PQ YA EL TOKEN VALIDA POR SI MISMO !!!
        	//una vez aquí, la firma del token fue validada, en   jwtTokenUtil.getAllClaimsFromToken(authToken)   (sino arroja exception),
        	//podemos setear en security context un ente,   SecurityContextHolder.getContext().setAuthentication(authentication);
        	
        	List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            //scopes.forEach(sc -> sc.forEach((k,v) -> authorities.add(new SimpleGrantedAuthority(v)))); //trabajamos esta estructura extraña de List<LinkedHashMap> pq así se guarda en el schema de seguridad de Spring y para no crear otra clase y usar Gson o Jackson
                //por alguna razón no sirvió forEach con esta estructura (ArrayList<ArrayList<LinkedHashMap<String,String>>>)
            for (ArrayList l0 : scopes) {
                for (Object o1 : l0) {
                    LinkedHashMap<String,String> hm = (LinkedHashMap<String,String>) o1;
                    for (String v : hm.values()) {
                        authorities.add(new SimpleGrantedAuthority(v));
                    }
                }
            }

            org.springframework.security.core.userdetails.User principal = new User(sub, "*", authorities);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            //authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("authenticated user " + sub + ", setting security context");
        }

        chain.doFilter(req, res);
    }
    
}
