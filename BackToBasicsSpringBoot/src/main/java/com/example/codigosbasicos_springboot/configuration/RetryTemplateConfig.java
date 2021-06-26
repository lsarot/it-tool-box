package com.example.codigosbasicos_springboot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.example.codigosbasicos_springboot.topics.retry_template.DefaultListenerSupport;

@Configuration
@EnableRetry
public class RetryTemplateConfig {

	@Bean
    public RetryTemplate retryTemplate() {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(4); // max retry attempts
        
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(3000); // SimpleRetryPolicy and having back to back retries can cause locking of the resources, so we should add a BackOff policy to create a gap between retries. 
        
        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);
        template.setListeners(new RetryListener[] { new DefaultListenerSupport() }); // template.registerListener(listener, index);
        return template;
    }
	
}
