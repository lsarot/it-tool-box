package com.example.rsocketserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class RsocketServerApplication {

	public static void main(String[] args) {
		//SpringApplication.run(RsocketServerApplication.class, args);
		
		new SpringApplicationBuilder()
        .main(RsocketServerApplication.class)
        .sources(RsocketServerApplication.class)
        //.profiles("server") //references application-server.properties file
        .run(args);
	}

}
