package com.example.springsecdemoJoinRSwithAS.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
     
    @Autowired
    private PasswordEncoder passwordEncoder;
 
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
    	oauthServer
    		.tokenKeyAccess("permitAll()")
    		.checkTokenAccess("isAuthenticated()");
    }
 
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    	clients
    	.inMemory()
    	.withClient("SampleClientId")
    	.secret(passwordEncoder.encode("secret"))
    	.authorizedGrantTypes("authorization_code")//we're only enabling a simple client using the authorization_code grant type.
    	.scopes("user_info")//notar que no hay ni read, ni write, usaríamos algo en HttpSecurityConfiguration.class tipo 		.antMatchers(HttpMethod.GET, "/user/info", "/api/emps/**").hasAuthority("SCOPE_read")
    	.autoApprove(true)//set to true so that we're not redirected and promoted to manually approve any scopes.
    	.redirectUris(
    			"http://localhost:8082/ui/login", 
    			"http://localhost:8083/ui2/login",
    			"http://localhost:8080/login",
    			"http://localhost:8080/webjars/springfox-swagger-ui/oauth2-redirect.html",
    			"http://localhost:8080/swagger-ui/oauth2-redirect.html");
    }
    
}
