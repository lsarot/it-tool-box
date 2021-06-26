package com.baeldung.greeter.autoconfigure;

import static com.baeldung.greeter.library.GreeterConfigParams.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baeldung.greeter.library.Greeter;
import com.baeldung.greeter.library.GreetingConfig;

/**
 * Look at /resources/META-INF/spring.factories
 * this file tells springboot to initialize this as per AutoConfiguration
 */

@Configuration
@ConditionalOnClass(Greeter.class) //initialize if Greeter.class is found on classpath
@EnableConfigurationProperties(GreeterProperties.class) //enables a Properties class
//@ConditionalOnMissingBean(type = "org.springframework.data.mongodb.MongoDbFactory")
public class GreeterAutoConfiguration {

    @Autowired private GreeterProperties greeterProperties;

    /**
     * here we configure the properties, based on defaults or on .properties file
    */
    @Bean
    @ConditionalOnMissingBean //just if a Bean of this type is not registered
    public GreetingConfig greeterConfig() {

        String userName = greeterProperties.getUserName() == null ? System.getProperty("user.name") : greeterProperties.getUserName();
        String morningMessage = greeterProperties.getMorningMessage() == null ? "Good Morning" : greeterProperties.getMorningMessage();
        String afternoonMessage = greeterProperties.getAfternoonMessage() == null ? "Good Afternoon" : greeterProperties.getAfternoonMessage();
        String eveningMessage = greeterProperties.getEveningMessage() == null ? "Good Evening" : greeterProperties.getEveningMessage();
        String nightMessage = greeterProperties.getNightMessage() == null ? "Good Night" : greeterProperties.getNightMessage();

        GreetingConfig greetingConfig = new GreetingConfig();
        greetingConfig.put(USER_NAME, userName);
        greetingConfig.put(MORNING_MESSAGE, morningMessage);
        greetingConfig.put(AFTERNOON_MESSAGE, afternoonMessage);
        greetingConfig.put(EVENING_MESSAGE, eveningMessage);
        greetingConfig.put(NIGHT_MESSAGE, nightMessage);
        return greetingConfig;
    }

    /**
     * the actual Bean to register as per AutoConfiguration
     * it will be injected and used in greeter-spring-boot-sample-app, which uses our starter
    */
    @Bean
    @ConditionalOnMissingBean
    public Greeter greeter(GreetingConfig greetingConfig) {
        return new Greeter(greetingConfig);
    }

}
