package com.example.rsocketclient.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.rsocketclient.RSocketShellClient;

@Configuration
public class BasicConfig {
	
	@Bean("RSocketShellClientLogger")
	public org.slf4j.Logger getLoggerRSocketShellClient() {
		return org.slf4j.LoggerFactory.getLogger(RSocketShellClient.class);
	}
	
}
