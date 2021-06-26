package com.sample.mybatis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * EN UNOS ENDPOINTS QUE SE EXPONEN AUTOMÁTICAMENTE SE FILTRAN CREDENCIALES DE OAUTH, HABRÁ QUE CHEQUEAR ESO
 * 
 * We will use the Springfox implementation of the Swagger 2 specification.
 * 
 * will be exposed on:	
 * http://localhost:8080/swagger-ui.html
 * 
 * No es necesario más nada. En los rest controllers podemos usar otras etiquetas para mayor configuración, pero es opcional.
 * 
 * usar hasta la versión 2.9.2 de SpringFox en SpringBoot 2.0.5 (por allí)
 * 		para SpringBoot 2.2.6 se necesitó la versión 3.0.0-SNAPSHOT
 * 
 * VER MÁS DETALLES EN PROYECTO SpringSwaggerDemo
 * */

@EnableSwagger2
@Configuration
public class SwaggerConfig {


    /**
     * Esto es similar a la anterior configuración con la librería swagger-springmvc, pero aquí en lugar de un SwaggerSpringMvcPlugin, tenemos un bean llamado Docket
     * */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("api-group-name(com.leo)")
                .apiInfo(apiInfo())
                //.directModelSubstitute(LocalDateTime.class, Date.class)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, getCustomResponseMessages())
                
                //It is not always desirable to expose the documentation for your entire API. You can restrict Swagger’s response by passing parameters to the apis() and paths() methods of the Docket class.
                .select()		//returns an instance of ApiSelectorBuilder, which provides a way to control the endpoints exposed by Swagger.
                .apis(RequestHandlerSelectors.any())		//Predicates for selection of RequestHandlers can be configured with the help of RequestHandlerSelectors and PathSelectors. Using any() for both will make documentation for your entire API available through Swagger.
                //.apis(RequestHandlerSelectors.basePackage("com.sample.mybatis.controller.rest"))		//controllers de este package solamente!
                .paths(PathSelectors.any())
                //.paths(PathSelectors.regex("/api.*"))
                //.paths(PathSelectors.ant("/api/v1/*"))
                .build()

                //OAUTH2 protected API
                //ESTOS POR SI LA API NO PERMITE EL ACCESO (Swagger to access an OAuth2-secured API – using the Authorization Code grant type)
                //https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
                //.securitySchemes(Arrays.asList(securityScheme()))
                //.securityContexts(Arrays.asList(securityContext()))
                ;
    }

    
    /**
     * API information
     * */
    private ApiInfo apiInfo() {
    	return new ApiInfoBuilder()
    			.title("My REST api")
    			.version("2.0")
    			.description("PoC of a REST api, MyApi")
    			.termsOfServiceUrl("http://www.mydomain/terms-of-service")
    			.contact(new Contact("contact-name","www.contact.com","user@gmail.com"))
    			.license("Apache License Version 2.0")
    			.licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
    			.build();
    }

    
    /**
     * CUSTOMIZED RESPONSE CODE MESSAGES
     * */
    private List<ResponseMessage> getCustomResponseMessages() {
        List<ResponseMessage> responseMessages = new ArrayList<>();

        responseMessages.add((new ResponseMessageBuilder()
                .code(500)
                .message("500 custom message")
                .responseModel(new ModelRef("Error"))
                .build()));

        responseMessages.add(new ResponseMessageBuilder()
                .code(403)
                .message("Forbidden!!!")
                .build());

        return responseMessages;
    }

    
    //------------- SECURITY WHEN IT IS AN OAUTH2 SECURED API

    
	//// FOR AN OAUTH SECURED API
    private static final String CLIENT_ID = "identifier";
    private static final String CLIENT_SECRET = "password";
    private static final String AUTH_SERVER = "server-url";
    
    
    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .scopeSeparator(" ")
                .useBasicAuthenticationWithAccessCodeGrant(true)
                .build();
    }

    
    /** 
     * This is used to describe how our API is secured (Basic Authentication, OAuth2, …)... In our case here, we'll define an OAuth scheme used to secure our Resource Server
     * */
    private SecurityScheme securityScheme() {
        GrantType grantType = new AuthorizationCodeGrantBuilder()
                .tokenEndpoint(new TokenEndpoint(AUTH_SERVER + "/token", "oauthtoken"))
                .tokenRequestEndpoint( new TokenRequestEndpoint(AUTH_SERVER + "/authorize", CLIENT_ID, CLIENT_SECRET))
                .build();

        SecurityScheme oauth = new OAuthBuilder().name("spring_oauth")
                .grantTypes(Arrays.asList(grantType))
                .scopes(Arrays.asList(scopes()))
                .build();
        return oauth;
    }

    
    /**
     * These sync up with the scopes we actually have defined in our application. (for the /foos API)
     * Scopes defined with spring security
     * */
    private AuthorizationScope[] scopes() {
        AuthorizationScope[] scopes = {
                new AuthorizationScope("read", "for read operations"),
                new AuthorizationScope("write", "for write operations"),
                new AuthorizationScope("foo", "Access foo API") };
        return scopes;
    }

    
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(
                        Arrays.asList(new SecurityReference("spring_oauth", scopes())))  //Note how the name we used here, in the reference – spring_oauth – syncs up with the name we used previously, in the SecurityScheme.
                //.forPaths(PathSelectors.regex("/api/v1/some-path.*"))
                .forPaths(PathSelectors.regex("/api/v1/.*"))
                //.forPaths(PathSelectors.regex("/foos.*"))
                .build();
    }
    

}
