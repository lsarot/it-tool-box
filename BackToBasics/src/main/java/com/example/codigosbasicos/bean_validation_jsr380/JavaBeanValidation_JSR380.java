package com.example.codigosbasicos.bean_validation_jsr380;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Basics of validating a Java bean with the standard framework – JSR 380, also known as Bean Validation 2.0.
 * 
 * JSR 380 is a specification of the Java API for bean validation, part of Jakarta EE and JavaSE, which ensures that the properties of a bean meet specific criteria, using annotations such as @NotNull, @Min, and @Max.
 * This version requires Java 8 or higher, and takes advantage of new features added in Java 8 such as type annotations, and supports new types like Optional and LocalDate.
 * */
public class JavaBeanValidation_JSR380 {
	
	public static void main(String[] args) {
		JavaBeanValidation_JSR380 instance = new JavaBeanValidation_JSR380();
		instance.start();
	}
	
	private void start() {
		
		//Some frameworks – such as Spring – have simple ways of triggering the validation process by just using annotations. This is mainly so that we don't have to interact with the programmatic validation API.
		//Let's now go the manual route and set things up programmatically:

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		//we set up a User with invalid (null) name value!
		User user = new User();
		user.setWorking(true);
		user.setAboutMe("Its all about me!");
		user.setAge(50);
		
		
		//OBTENEMOS UN SET CON LOS ERRORES ENCONTRADOS SOBRE LA VALIDACIÓN
		Set<ConstraintViolation<User>> violations = validator.validate(user);
		
		for (ConstraintViolation<User> violation : violations) {
		    System.err.println(violation.getMessage()); 
		}
		
	}

}
