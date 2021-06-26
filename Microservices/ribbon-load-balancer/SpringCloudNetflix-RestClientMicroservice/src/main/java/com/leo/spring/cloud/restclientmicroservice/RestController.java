package com.leo.spring.cloud.restclientmicroservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

/**
 * we use the RibbonClient to enable the load balancing instead of the plain RestTemplate
 *  Inject a SpringClientFactory to access the client that is created.
 * */

@org.springframework.web.bind.annotation.RestController
@RibbonClient(
		  name = "ping-a-server",
		  configuration = RibbonConfiguration.class)
public class RestController {
	
	/**
	 * LoadBalanced is to mark a RestTemplate or WebClient bean to be configured to use a LoadBalancerClient.
	 * Annotate the RestTemplate with @LoadBalanced which suggests that we want this to be load balanced and in this case with Ribbon.
	 * */
	@LoadBalanced
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

	
    @Autowired RestTemplate restTemplate;

    @GetMapping("/server-location")
    public String serverLocation() {
        return this.restTemplate.getForObject("http://ping-server/test", String.class);
    }

}
