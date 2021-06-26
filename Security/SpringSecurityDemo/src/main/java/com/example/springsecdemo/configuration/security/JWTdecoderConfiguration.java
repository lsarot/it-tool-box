package com.example.springsecdemo.configuration.security;

import java.util.Collections;
import java.util.Map;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * NECESARIO PARA EL RESOURCE SERVER (RS) para inyectar el JWT (ie. en un método rest)
 * */

@Configuration
public class JWTdecoderConfiguration {

	@Bean
	public JwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
		NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
			.withJwkSetUri(properties.getJwt().getJwkSetUri())// obtenida del .yml -> http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/certs
			.build();
		jwtDecoder.setClaimSetConverter(new OrganizationSubClaimAdapter());
		return jwtDecoder;
	}
	

	public static class OrganizationSubClaimAdapter implements Converter<Map<String, Object>, Map<String, Object>> {
		
		private final MappedJwtClaimSetConverter delegate = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

		public Map<String, Object> convert(Map<String, Object> claims) {

			Map<String, Object> convertedClaims = this.delegate.convert(claims);

			String organization = convertedClaims.get("organization") != null ? 
					(String) convertedClaims.get("organization")
					: "unknown";
			convertedClaims.put("organization", organization.toUpperCase());

			return convertedClaims;
		}
		
	}
	
}
