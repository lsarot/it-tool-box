package com.example.springsecconfigurehttps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;


@SpringBootApplication//(exclude = {
			// SecurityAutoConfiguration.class,
			//})
@PropertySource("classpath:application-defaults.properties")
public class SpringSecConfigureHttpsApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SpringSecConfigureHttpsApplication.class);
        application.setAdditionalProfiles("ssl");
        application.run(args);
	}
}

/** ENABLE A SELF-SIGNED (snake oil) CERTIFICATE IN APACHE ON UBUNTU for testing
 * 
 * 1. Load the SSL module:
 * 		sudo a2enmod ssl
 * 2. Enable default SSL config:
 * 		sudo a2ensite default-ssl.conf
 *--this configuration will set the SELengine to On and make use of a self-signed cert and key.
 *3. Restart Apache:
 * 		sudo systemctl restart apache2
 * */
