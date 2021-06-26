package com.example.springsecdemo.controller.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.springsecdemo.model.persistence.entity.Employee;
import com.example.springsecdemo.service.EmployeeService;

@CrossOrigin(origins = "http://localhost:8089") // this is the controller-level config we need to allow CORS for our Angular App running at the specified URL. (SOBRE MÉTODO O SOBRE CLASE)
@RestController
@RequestMapping(value = "/api/emps")
public class EmployeeController {

	@Autowired
	private EmployeeService empSrv;
	
	
    @GetMapping(value = "/{id}")
    public Collection<Employee> findOne(@PathVariable Long id) {
		Employee entity = empSrv.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return Arrays.asList(entity.toDto());
    }
 
    @GetMapping
    public Collection<Employee> findAll() {
        Iterable<Employee> emps = this.empSrv.findAll();
        List<Employee> empDtos = new ArrayList<>();
        emps.forEach(p -> empDtos.add(p.toDto()));
        return empDtos;
    }
    
    
    /** ESTO ES DEL RS
     * Habíamos visto que el ResourceSrv validaba el JWT con el AuthorizationSrv, y permitía acceder su endpoint (que ofrece info del usuario)
     * Ahora, así recuperamos el Token y extraemos unos campos del payload.
     * 
     * En el otro proyecto SpringWebDemo, en un endpoint recuperábamos el Principal, que es el usuario de dicha sesión, (en SpringWebDemo era para ver el usuario creado en la bbdd, nada con OAuth)
     * 		pero en ese caso usamos validación directa con el servidor, no con un JWT, ya sea expedido por un AuthorizationServer o por nosotros mismos.
     * 
     * Para identificar al cliente esta vez, inyectamos el JWT en el endpoint.
     * 
     * Inyectar ese Jwt necesita una configuración adicional, expuesta en JWTdecoderConfiguration.java
     */
    @GetMapping("/user/info-from-jwt")
    public Map<String, Object> getUserInfoFromJWT(@AuthenticationPrincipal Jwt principal) {
		Map<String, String> map = new Hashtable<String, String>();
		map.put("user_name", principal.getClaimAsString("preferred_username"));
		map.put("organization", principal.getClaimAsString("organization"));
		
		map.entrySet().stream()
				.forEach(entry -> {
					System.out.println(entry.getKey() + " : " + entry.getValue());
				});
		
        return Collections.unmodifiableMap(map);
    }
    
}
