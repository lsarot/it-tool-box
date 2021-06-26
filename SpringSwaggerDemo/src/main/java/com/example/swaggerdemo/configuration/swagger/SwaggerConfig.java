package com.example.swaggerdemo.configuration.swagger;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;

import com.example.swaggerdemo.configuration.swagger.plugin.EmailAnnotationPlugin;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.AuthorizationCodeGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.TokenEndpoint;
import springfox.documentation.service.TokenRequestEndpoint;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

//@Import({
	//SpringDataRestConfiguration.class, //de versión 3.0.0 (la 2.9.2 también lo tiene pero parece que no va muy bien!)
			//si agregamos un @Entity y su repository interface (ie. extends CrudRepository), se agrega directamente el controller rest (notar que no está creado para User entity, pero en el swagger sí existe).
			//pudiera ser que esto sucede incluso sin swagger, pero no nos dábamos cuenta!, es decir, se exponen operaciones CRUD para las @Entity que tengamos en el proyecto (requiere aplicar seguridad)
	BeanValidatorPluginsConfiguration.class, //Support for JavaBeanValidation JSR303, mostrará restricciones de un bean en el swagger.. specifically for @NotNull, @Min, @Max, and @Size.
			//ie. en método POST de /users, vamos a Model, ahora aparece firstName* y age con minimum y maximum. 
	//})
//In plain Spring projects, we need to enable Swagger 2 explicitly. To do so, we have to use the @EnableSwagger2WebMvc on our configuration class
//@EnableSwagger2 //until v2.9.2
//@EnableSwagger2WebMvc //v3.0.0-SNAPSHOT
@Configuration
public class SwaggerConfig {

	
	/** CON ESTO YA NOS BASTA PARA ACTIVAR SWAGGER
	 * todo lo demás del proyecto añade características extras
	 * 
	 * ver más configuración del Docket en proyecto SpringMyBatisDemo
	 * 
	 * The configuration of Swagger mainly centers around the Docket bean.
	 * After defining the Docket bean, its select() method returns an instance of ApiSelectorBuilder, which provides a way to control the endpoints exposed by Swagger.
	 * We can configure predicates for selecting RequestHandlers with the help of RequestHandlerSelectors and PathSelectors. Using any() for both will make documentation for our entire API available through Swagger.
	 * */
	@Bean
    public Docket api() {
		//http://springfox.github.io/springfox/docs/current/#quick-start-guides
        return new Docket(DocumentationType.SWAGGER_2)
          .select()
          .apis(RequestHandlerSelectors.any()) //RequestHandlerSelectors.basePackage("com.baeldung.web.controller")
          .paths(PathSelectors.any()) //any(), none(), regex(), or ant()
          .build()
          //OAUTH2 protected API
          		//desactivando esto y en HttpSecurityConfiguration usando permitAll, ya deshabilito la seguridad
          .securitySchemes(Arrays.asList(securityScheme()))
          .securityContexts(Arrays.asList(securityContext()))
          ;
    }
	
	
    /* SI NO USAMOS SPRING BOOT
     * Without Spring Boot, you don't have the luxury of auto-configuration of your resource handlers.
     * Swagger UI adds a set of resources which you must configure as part of a class that extends WebMvcConfigurerAdapter, and is annotated with @EnableWebMvc.
     *
    @Configuration
	@EnableWebMvc
	public class RestConfiguration extends WebMvcConfigurerAdapter {
    	@Override
    	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	        registry.addResourceHandler("swagger-ui.html")
	        	.addResourceLocations("classpath:/META-INF/resources/");
	     
	        registry.addResourceHandler("/webjars/**")
	        	.addResourceLocations("classpath:/META-INF/resources/webjars/");
	        	
	        registry.addResourceHandler("/swagger-ui/**")
	       		.addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/2.1.3/");
	    }
	}
    */
	
	
	/** PLUGINS
	 * Podemos añadir características adicionales al API con un Plugin de SpringFox.. A plugin can offer various features, from enriching the models and properties to the custom API listings and defaults.
	 * 
	 * Springfox supports the plugin creation through its spi module. The spi module provides a few interfaces like the 
	 * 		ModelBuilderPlugin
	 * 		ModelPropertyBuilderPlugin
	 * 		ApiListingBuilderPlugin
	 * that act as an extensibility hook to implement a custom plugin.
	 * 
	 * vamos a enriquecer la validación @Email usada en User entity, a partir de un Plugin de tipo ModelPropertyBuilderPlugin
	 * ver EmailAnnotationPlugin
	 * */
	@Bean
    public EmailAnnotationPlugin emailPlugin() {
        return new EmailAnnotationPlugin();
    }
	
	
	//------------- SECURITY WHEN IT IS AN OAUTH2 SECURED API

	
	////FOR AN OAUTH SECURED API
   private static final String CLIENT_ID = "SampleClientId";
   private static final String CLIENT_SECRET = "secret";
   private static final String USER_AUTHORIZATION_URI = "http://localhost:8081/auth/oauth/authorize";
   private static final String ACCESS_TOKEN_URI = "http://localhost:8081/auth/oauth/token";
   
    
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
                .tokenEndpoint(new TokenEndpoint(ACCESS_TOKEN_URI, "oauthtoken"))
                .tokenRequestEndpoint( new TokenRequestEndpoint(USER_AUTHORIZATION_URI, CLIENT_ID, CLIENT_SECRET))
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
     * 
     * podemos agregar los que queramos, pero para que funcione debe estar registrado en el AS,
     * mirar el AS, la clase AuthServerConfig, método configure(ClientDetailsServiceConfigurer clients), .scopes
     * */
    private AuthorizationScope[] scopes() {
        AuthorizationScope[] scopes = {
                new AuthorizationScope("read", "for read operations"),
                new AuthorizationScope("write", "for write operations"),
                new AuthorizationScope("foo", "Access foo API"),
                new AuthorizationScope("user_info", "Must grant this one!")};
        return scopes;
    }

    
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(
                        Arrays.asList(new SecurityReference("spring_oauth", scopes())))  //Note how the name we used here, in the reference – spring_oauth – syncs up with the name we used previously, in the SecurityScheme.
                //.forPaths(PathSelectors.regex("/api/v1/.*"))
                .forPaths(PathSelectors.regex("/.*"))
                //.forPaths(PathSelectors.ant("/**"))
                //.forPaths(PathSelectors.none())
                .build();
    }
	
}
