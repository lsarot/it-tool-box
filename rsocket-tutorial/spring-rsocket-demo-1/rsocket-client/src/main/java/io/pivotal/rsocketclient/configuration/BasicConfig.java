package io.pivotal.rsocketclient.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.pivotal.rsocketclient.RSocketShellClient;

@Configuration
public class BasicConfig {
	
	@Bean("RSocketShellClientLogger")
	public org.slf4j.Logger getLoggerRSocketShellClient() {
		return org.slf4j.LoggerFactory.getLogger(RSocketShellClient.class);
	}
	
}
