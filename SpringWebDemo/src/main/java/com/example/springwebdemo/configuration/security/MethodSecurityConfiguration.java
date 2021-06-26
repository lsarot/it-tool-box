package com.example.springwebdemo.configuration.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

////@EnableWebSecurity will provide configuration via HttpSecurity providing the configuration you could find with <http></http> tag in xml configuration, it's allow you to configure your access based on urls patterns, the authentication endpoints, handlers, etc...
////@EnableGlobalMethodSecurity provides AOP security on methods, some of annotation it will enable are PreAuthorize PostAuthorize also it has support for JSR-250.
@Configuration
@EnableGlobalMethodSecurity( 
		prePostEnabled = true,// allows for using PreAuthorize and PostAuthorize on methods
		securedEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
	
}