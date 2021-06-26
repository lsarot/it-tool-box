package io.pivotal.rsocketserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.pivotal.rsocketserver.RSocketController;

@Configuration
public class BasicConfig {
	
	@Bean("RSocketControllerLogger")
	public org.slf4j.Logger getLoggerRSocketController() {
		return org.slf4j.LoggerFactory.getLogger(RSocketController.class);
	}
	
}
