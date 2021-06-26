package com.example.rsocketserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.rsocketserver.RSocketController;

@Configuration
public class BasicConfig {
	
	@Bean("RSocketControllerLogger")
	public org.slf4j.Logger getLoggerRSocketController() {
		return org.slf4j.LoggerFactory.getLogger(RSocketController.class);
	}
	
}