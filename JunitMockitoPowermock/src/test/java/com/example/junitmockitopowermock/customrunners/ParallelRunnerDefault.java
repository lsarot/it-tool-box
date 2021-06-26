package com.example.junitmockitopowermock.customrunners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.example.junitmockitopowermock.paralleltestsexecution.ClassUnderTestA;
import com.example.junitmockitopowermock.paralleltestsexecution.ClassUnderTestA.ClassUnderTestA_Inner;


/** HABLANDO DE CUSTOM JUNIT RUNNERS
 * 
 * We can extend one of the specialized subclasses of Runner: ParentRunner or BlockJUnit4Runner.
 * 		.The abstract ParentRunner class runs the tests in a hierarchical manner.
 * 		.BlockJUnit4Runner is a concrete class and if we prefer to customize certain methods, we'll probably be extending this class.
 * 
 * Runner
 * 		ParentRunner
 * 			BlockJUnit4ClassRunner
 * 			|Suite
 * 				Parameterized
 * 
 * 	MyCustomRunner debe extender de alguna
 * 
 * If we only want to make minor changes it is a good idea to have a look at the protected methods of BlockJUnit4ClassRunner.
 * 
 * Otros third-party:
 * SpringJUnit4ClassRunner
 * MockitoJUnitRunner
 * HierarchicalContextRunner
 * CucumberRunner 
 * ...
 * */

public class ParallelRunnerDefault extends Runner {
	 
    private Class testClass;
    
    private static volatile Boolean alreadyStarted = false;
    
    public ParallelRunnerDefault(Class testClass) {
        super();
        this.testClass = testClass;
    }
 
    @Override
    public Description getDescription() {
        return Description.createTestDescription(testClass, "Basic Parallel Runner, extends from Runner.");
    }
 
    @Override
    public void run(RunNotifier notifier) {
    	synchronized (alreadyStarted) {
    		if (!alreadyStarted) {
    			alreadyStarted = true;
    			System.out.println("running the tests from ParallelRunnerDefault: " + testClass);
    			
    			runInParallel(notifier);
    		}
    	}
    	
        //runEachByReflection(notifier);
    }
    
    
    /** LO IDEAL ES NO REPETIR LO QUE YA ESTÁ HECHO..
	 *  EL PROPÓSITO DE ESTA DEMO ES USAR EL PARALELISMO DE ParallelComputer de JUnit, NO reinventar el llamado secuencial de métodos que ya trae los otros runners.
	 *  SE HIZO A MODO DIDÁCTICO!
     * 
     * Como se llaman a los métodos directamente por reflección, los resultados aparecen por clase, pero clasificados como Unrooted Tests
     * 
     * */
    private void runEachByReflection(RunNotifier notifier) {
    	try {
			// POR REFLEXIÓN
    	
			//PRIMERO LOS MÉTODOS DE LA CLASE
    		
			Object outerInstance = testClass.newInstance();//PUEDO EVALUAR SI TIENE ANNOTATION SOBRE CLASE PARA USAR PER_CLASS O PER_METHOD Y CREAR UNA INSTANCIA NUEVA PARA CADA MÉTODO!!!
			for (Method method : testClass.getMethods()) {
				if (method.isAnnotationPresent(Test.class)) {//annotation @Test es dif en JUnit 4 y 5, si importamos de JUnit 5 podremos poner métodos protected, public y friendly
					notifier.fireTestStarted(Description.createTestDescription(testClass, method.getName()));
					method.invoke(outerInstance);
					notifier.fireTestFinished(Description.createTestDescription(testClass, method.getName()));
				}
			}
			
			//LUEGO LOS DE LAS CLASES ANIDADAS
			
			Class[] innerClasses = testClass.getClasses();
			if (innerClasses.length > 0) {
				for (Class innerClass : innerClasses) {
					//si es static la inner
					//Object innerInstance = inner.newInstance();
		            //si no es static
					//Class<?> inner = Class.forName(inner.getName());
		            Constructor<?> ctor = innerClass.getDeclaredConstructor(testClass);
		            Object innerInstance = ctor.newInstance(outerInstance);
					
					for (Method method : innerClass.getMethods()) {
						if (method.isAnnotationPresent(Test.class)) {
							notifier.fireTestStarted(Description.createTestDescription(innerClass, method.getName()));
							method.invoke(innerInstance);
							notifier.fireTestFinished(Description.createTestDescription(innerClass, method.getName()));
						}
					}
				}
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    

    /**
    * Pudiéramos simplemente llamar por reflexión a cada método programando su llamado en un runnable/callable y un pool de threads.
	*		//como en el otro método, pero agendando los llamados al pool
	*		//así nos permitimos usar categorías y paralelismo completo de métodos y clases.
	
	* Please note that, this will run all test cases in parallel, if you have any dependencies between different test cases it might result in false positives.
	* You SHOULD NOT have interdependent tests anyway.
	
	* QUEREMOS USAR JUnit ParallelComputer PARA EJECUTAR EN PARALELO. (JUnit 4.12+)
	* JUnit jupiter (5+) tiene un package org.junit.jupiter.api.parallel que pudiera tener otras cosas
	
	* NO SE MUESTRAN RESULTADOS DE JUNIT EN EL IDE, debemos imprimir los resultados en consola! Hay que utilizar mejor el notifier!

	* SE EJECUTAN CLASES QUE TENGAN ETIQUETA Category SI EL USUARIO DEL RUNNER LAS ESPECIFICA EN getTestCategoriesToRunUponTestClassesToRun
	
 	* OTRAS LIBRERÍAS:
	
	* La librería Tempus-Fugit requiere usar @RunWith(ConcurrentTestRunner)
	* impidiendo que use otro runner como el de PowerMock
	
	
	* Se menciona un plugin de terceros para Maven: (esto es programático, no por Maven)
	* 
	* Another choice: Punner, a new parallel junit runner and maven plugin.
	* <groupId>com.github.marks-yag</groupId>
	* <artifactId>punner-maven-plugin</artifactId>
	
	
	 * https://github.com/michaeltamm/junit-toolbox
	 * Una herramienta de tercero
	 
     * */
    private void runInParallel(RunNotifier notifier) {
    	try {
    		//THREADS EN PARALELO
    		//EN VEZ DE LLAMAR UNO POR UNO CON REFLEXIÓN

    		//BUSCAMOS CLASES
    		
    		Object outerInstance = testClass.newInstance();
    		
			Method methodTestClasses = testClass.getMethod("getTestClassesToRun", null);
			Class<?>[] classes0 = (Class<?>[]) methodTestClasses.invoke(outerInstance);

			if (classes0.length == 0 || classes0 == null)
				throw new RuntimeException("Debe declarar test-classes a ejecutar!.");
			
			//BUSCAMOS CATEGORÍAS

			Method methodCateg = testClass.getMethod("getTestCategoriesToRunUponTestClassesToRun", null);
			Class<?>[] categories = (Class<?>[]) methodCateg.invoke(outerInstance);
			
			if (categories != null && categories.length != 0) {//si establecimos categorías
				List classes1 = Arrays.asList(classes0);//clases a testear
				List categories1 = Arrays.asList(categories);//categorías a testear
				List classes2 = new ArrayList<>();
				
				for (Class<?> class0 : classes0) {//para cada clase a testear
					org.junit.experimental.categories.Category cat =  class0.getAnnotation(org.junit.experimental.categories.Category.class);
					if (cat != null) {
						Class<?>[] classCategs = cat.value();
						for (Object categ : classCategs) {//cada cat de la clase
							if (categories1.contains(categ)) {//si está incluída en las categorias a testear
									if (!classes2.contains(class0)) {//añado a clase a testear 1 sóla vez
										classes2.add(class0);
									}
							}
						}
					}
				}
				
				classes0 = new Class[classes2.size()];
			    classes2.toArray(classes0);
			}
			
			
			//no ví cambio (sobre ver resultados detallados)
			//notifier.fireTestSuiteStarted(Description.createSuiteDescription(testClass.getClass()));
			//notifica para una clase particular, no serviría
			//notifier.fireTestStarted(Description.createTestDescription(testClass.getClass(), testClass.getClass().getSimpleName()));
	  		//notifier.fireTestFinished(Description.createTestDescription(testClass.getClass(), testClass.getClass().getSimpleName()));
			
	  		Result result = JUnitCore.runClasses(new ParallelComputer(true, true), classes0);

	  		//notifier.fireTestSuiteFinished(Description.createSuiteDescription(testClass.getClass()));
	  		
			// ParallelComputer(true,true) will run all classes and methods in parallel.
			//(First arg for classes, second arg for methods) 
			//HABLA DE CLASES INTERNAS, pq las externas no tiene sentido ejecutando explícitamente este test-class.
			//a menos que hagamos un test-class genérico vacío, ejecuta con mi Runner y este analiza todas las clases de la ruta de tests!!!.
    		
	  		
			//IMPRIMIR RESULTADOS
			List<Failure> failures = result.getFailures();
			for (Failure failure : failures) {
				System.err.println("Failure: " + failure.getTestHeader());
				System.err.println(failure.getDescription().getClassName() + " . " + failure.getDescription().getMethodName());
				System.err.println("Failure message: " + failure.getMessage());
				System.err.println(failure.getTrace());
			}
    		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
}