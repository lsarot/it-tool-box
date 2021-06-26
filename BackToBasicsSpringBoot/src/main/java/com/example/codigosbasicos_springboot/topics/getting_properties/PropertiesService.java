package com.example.codigosbasicos_springboot.topics.getting_properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

// will store all properties on Environment object
@PropertySource("classpath:application.properties")
@PropertySource("file:${HOME}/test_properties/conf.properties") //or specify yaml file     ("file:/Users/Leo/test_properties/conf.properties")
@Configuration
@Service
public class PropertiesService {

	@Autowired private Environment env;

	public String getProperty_1(String key) {
		return env.getProperty(key);
	}
	
}
