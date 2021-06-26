package com.example.codigosbasicos.java_8;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Java8FeaturesTest {

	public static void main (String... args) {
		
		Java8FeaturesTest instance = new Java8FeaturesTest();

		instance.streams();
		
		instance.functionalInterface(); // try to use standards from Java8, package java.util.function
		instance.ownFunctionalInterface();
		  
		instance.defaultInterfaceMethodAndMultipleExtendsInterface(); //try not to overuse default method
		
		instance.dontOverloadInterfaceMethodsWithFuncionalInterfAsParams();
		
		instance.methodReferences();//Class/instance :: method
		
	}
	

	//-------------------------------------------------------------------------
	
	private void streams() {
		Iterable<String> iterable = Arrays.asList("Testing", "Iterable", "conversion", "to", "Stream");
		
		List<String> result = StreamSupport.stream(iterable.spliterator(), false)
				.filter(item -> item.startsWith("T"))
				.map(String::toUpperCase)
				//.reduce()
				.collect(Collectors.toList());
		
		System.out.println(result);
	}
	
	//-------------------------------------------------------------------------
	
	/**
	 * LAMBDAS
	 * */
	private void functionalInterface() {
		Function<String, String> func = param -> "Hi " + param; //this way of declaring, instead of innerClass like new myInterface() {} is less verbose
			//The lambda expression approach can be used for any suitable interface from old libraries. It is usable for interfaces like Runnable, Comparator, and so on.
		
		//Inner class no funciona igual que Lambda
		//Las inner class tienen su propio scope, puedes declarar variables dentro pq son una clase... Para acceder a una fuera tienes que crear una impl y pasarsela por constructor por ej o declarar la externa como final
		//las Lambda acceden al enclosing scope (exterior)... Accede a la var exterior sin problemas
		
		
		//Si tienes muchas líneas de código en el body, pásalo a un método y llama a este desde la lambda. () -> other(args)
		
		//(a, b) -> a.toLowerCase() + b.toLowerCase(); uses type inference, but one can use (String a, String b) if necessary
		// a -> a.toLowerCase() //si es 1 arg, no requiere paréntesis
		
		
		System.out.println(functionalInterface_A("Leo", func));
	}
	private String functionalInterface_A(String name, Function<String, String> f) {
		return f.apply(name);
	}
	
	/** Sólo escribir la nuestra si realmente no existe una en el paquete java.util.function standard
	 * */
	private void ownFunctionalInterface() {
		Foo foo = param -> "Hi " + param;
		System.out.println(ownFunctionalInterface_A("Leo", foo));
	}
	private String ownFunctionalInterface_A(String name, Foo f) {
		return f.method(name);
	}
	@FunctionalInterface //if my interface only has 1 method, this wont be necessary!, but better to convey that the interface was meant to be used as functional if someone decides to add another method
	public interface Foo {
		String method (String s);
	}

	//-------------------------------------------------------------------------
	
	private void defaultInterfaceMethodAndMultipleExtendsInterface() {
		FooExtended fooe = () -> "Hola";
		System.out.println(fooe.method());
		fooe.defaultBaz();
		fooe.defaultBar();
	}
	@FunctionalInterface
	public interface FooExtended extends Baz, Bar {//extends from multiple interfaces
		//while both parent interfaces use same abstract method name it is possible
		//while both parents use different default method names
			//if they don't, we have to override defaultMethod, and if we want to call a parent impl., we use Baz.super.defaultMethod()
	}
	@FunctionalInterface
	public interface Baz {  
	    String method();    
	    default void defaultBaz() {System.out.println("In Baz default");}//default method
	}
	@FunctionalInterface
	public interface Bar {  
	    String method();    
	    default void defaultBar() {System.out.println("In Bar default");}    
	}
	
	//-------------------------------------------------------------------------
	
	private void dontOverloadInterfaceMethodsWithFuncionalInterfAsParams() {
		//Si usamos el llamado abreviado (sin inner class), 		processorImpl.process( () -> "abc" )	ambos métodos lo aceptan, pero habrá conflicto
		//usamos nombres distintos (preferido), o hacemos 	processorImpl.process( (Supplier<String>) () -> "abc" )		(no preferido)
	}
	public interface Processor {
	    String process(Callable<String> c) throws Exception;
	    String process(Supplier<String> s);
	}
	
	//-------------------------------------------------------------------------
	
	private void methodReferences() {
		//There are four kinds of method references:
			//					Kind 																																	Example
			//Reference to a static method 																							ContainingClass::staticMethodName
			//Reference to an instance method of a particular object 												containingObject::instanceMethodName
			//Reference to an instance method of an arbitrary object of a particular type 				ContainingType::methodName
			//Reference to a constructor 																								ClassName::new
		
		//https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
		
		//FALTA ESTUDIAR EN PROFUNDIDAD!
	}

	//-------------------------------------------------------------------------
	
	
}
