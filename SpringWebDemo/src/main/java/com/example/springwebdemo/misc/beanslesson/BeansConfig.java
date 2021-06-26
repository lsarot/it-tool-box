package com.example.springwebdemo.misc.beanslesson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BeansConfig {

	////SAY WE HAVE
	public interface Animal {}
	public class Tiger implements Animal {
		public String name;
		public Tiger (String name) {this.name=name;}
		public Tiger () {}
	}
	public class Lion implements Animal {
		public String name;
		public Lion (String name) {this.name=name;}
	}
	
	//-------------------------------------------
	
	//BEAN SCOPES https://www.baeldung.com/spring-bean-scopes
	
	@Bean(name = {"tiger", "kitty"}) //names for future reference
	@Scope(value = "prototype") //for multiple instances
	Tiger getTiger(String name) {
		return new Tiger(name);
	}
	
	@Bean(name = "lion") 
	@Scope("singleton") //default SINGLETON scope, no need to declare
	Lion getLion() {
		return new Lion("Hardcoded lion name");
	}

}
