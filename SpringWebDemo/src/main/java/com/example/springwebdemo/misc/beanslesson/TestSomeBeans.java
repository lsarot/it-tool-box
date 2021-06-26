package com.example.springwebdemo.misc.beanslesson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.example.springwebdemo.misc.beanslesson.BeansConfig.Lion;
import com.example.springwebdemo.misc.beanslesson.BeansConfig.Tiger;

/**
 * https://www.baeldung.com/spring-getbean
 * 
 * Despite being defined in the BeanFactory interface, the getBean() method is most frequently accessed through the ApplicationContext. 
 * Typically, we don't want to use the getBean() method directly in our program.
 * Beans should be managed by the container. 
 * If we want to use one of them, we should rely on dependency injection rather than a direct call to ApplicationContext.getBean(). 
 * That way, we can avoid mixing application logic with framework-related details.
 * */
public class TestSomeBeans {

	//@Autowired private ApplicationContext context;
	
	public void testSomeBeans(ApplicationContext context) {//SE LO PASAMOS AQUÍ PQ NO SABEMOS AÚN PQ NO LO INYECTA!
		//1. Retrieving Bean by Name
		Tiger tiger = (Tiger) context.getBean("kitty", "kittito"); //USARÁ UN @Bean declarado con name="kitty" y le pasará el argumento "kittito"
		
		//2. Retrieving Bean by Name and Type
		Lion lion = context.getBean("lion", Lion.class);//llamado lion, de tipo Lion.class
		
		//3. Retrieving Bean by Type
		Lion lion2 = context.getBean(Lion.class);
					//context.getBean(Animal.class) //will throw exc cause will find several types (Tiger, Lion)
		
		//4. Retrieving Bean by Name with Constructor Parameters (ONLY FOR PROTOTYPE SCOPE BEANS)
		Tiger tiger2 = (Tiger) context.getBean("tiger", "Siberian");
		Tiger secondTiger = (Tiger) context.getBean("tiger", "Striped");
		
		//5. Retrieving Bean by Type With Constructor Parameters (ONLY FOR PROTOTYPE SCOPE BEANS)
		Tiger tiger3 = context.getBean(Tiger.class, "Shere Khan");//de tipo Tiger, le pasamos Shere Khan
		
		System.out.println("\nAnimals -> " + String.join("|", tiger.name, lion.name, lion2.name, tiger2.name, secondTiger.name, tiger3.name) + "\n");
	}

	
}
