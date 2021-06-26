package com.example.junitmockitopowermock.mavenwithtests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

/**
 * EL OBJETIVO DE ESTOS TESTS ERA PROBAR EJECUTAR TESTS EN PARALELO AL COMPILAR CON MAVEN, NO DESDE EL IDE.
 * Y EJECUTAR CATEGORÍAS DE TESTS.
 * 
 * PODEMOS EJECUTAR LOS TESTS CUANDO COMPILAMOS EL PROYECTO
 * con mvn install sobre la ruta del pom.xml
 * Se hace esto para trabajos de despliegue, usando ie. Jenkins
 * Ya que el desarrollador los ejecutará principalmente en su IDE
 * 
 * En el pom, tenemos el plugin de maven surfire
 * 
 * JUnit 4.7 and later versions make it possible to execute tests in parallel using Maven's Surefire Plugin. In a nutshell, Surefire provides two ways of executing tests in parallel:
    - The first approach uses multithreading inside a single JVM process
    - While the second approach uses multiple JVM processes
	We'll cover how to configure Surefire to run JUnit tests in parallel inside a single JVM process.
	
	To run a test in parallel we should use a test runner that extends org.junit.runners.ParentRunner.
		However, even tests that don't declare an explicit test runner work, as the default runner extends this class.
	
	<parallel>value</parallel>
	The possible values are:
	    methods – runs test methods in separate threads
	    classes – runs test classes in separate threads
	    classesAndMethods – runs classes and methods in separate threads
	    suites – runs suites in parallel
	    suitesAndClasses – runs suites and classes in separate threads
	    suitesAndMethods – creates separate threads for classes and for methods
	    all – runs suites, classes as well as methods in separate threads
	    
	<threadCount>10</threadCount><!--defines the maximum number of threads Surefire will create-->
	<useUnlimitedThreads>true</useUnlimitedThreads><!--one thread is created per CPU core-->
	<perCoreThreadCount>true</perCoreThreadCount><!--By default, threadCount is per CPU core. We can use the parameter perCoreThreadCount to enable or disable this behavior-->
	<threadCountSuites>2</threadCountSuites><!--limitations over granularity thread creation. When declared, it reigns over using unlimited threads-->
	<threadCountClasses>2</threadCountClasses>
	<threadCountMethods>6</threadCountMethods>
	<parallelTestTimeoutForcedInSeconds>5</parallelTestTimeoutForcedInSeconds><!--This will interrupt currently running threads and will not execute any of the queued threads after the timeout has elapsed-->
	<parallelTestTimeoutInSeconds>3.5</parallelTestTimeoutInSeconds><!--In this case, only the queued threads will be stopped from executing-->
	
	Surefire calls static methods annotated with @Parameters, @BeforeClass, and @AfterClass in the parent thread. Thus make sure to check for potential memory inconsistencies or race conditions before running tests in parallel.
		Also, tests that mutate shared state are definitely not good candidates for running in parallel.
	
	***Test Execution in Multi-Module Maven Projects
	Till now, we have focused on running tests in parallel within a Maven module.
	But let's say we have multiple modules in a Maven project. Since these modules are built sequentially, the tests for each module are also executed sequentially.
	We can change this default behavior by using Maven's -T parameter which builds modules in parallel. This can be done in two ways.
		We can either specify the exact number of threads to use while building the project:
			mvn -T 4 surefire:test
		Or use the portable version and specify the number of threads to create per CPU core:
			mvn -T 1C surefire:test
  
 	
 		Probar:
 		mvn -P unProfileName install
 		mvn -P unProfileName test
 		
 		
 		
 		NO ME FUNCIONÓ CON MAVEN TODAVÍA!!!
 		
 * */

@DisplayName("Maven Build With Tests Execution")
@Category(com.example.junitmockitopowermock.categories.CatSlowTests.class)
@TestInstance(Lifecycle.PER_METHOD)
public class MavenBuildWithTests {

	/*@Category(fully.qualified.class.name.class)   (JUnit 4.8+)
	 * la categoría es una clase o interface
	 * Se ejecutan los tests anotados con dicha categoría, y las categorías que extiendan de esta si aplica.
	 * Se puede poner a nivel de clase y/o métodos.
	 * A partir de JUnit4.12 y plugin maven surefire 2.18.1, si colocas a nivel de clase, se aplica también a las que hereden, es decir, clases tests que extiendan de otra clase test con dicha @Category.
	 * */
	
	
	@Test
	@Category(com.example.junitmockitopowermock.categories.CatSlowTests.class)
	void testSlow() {
		System.out.println("slow");
		Assertions.assertTrue(true);
	}
	
	@Test
	@Category(com.example.junitmockitopowermock.categories.CatSlowerTests.class)
	void testSlower() {
		System.out.println("slower");
		Assertions.assertTrue(true);
	}
	
	@Test
	@Category(com.example.junitmockitopowermock.categories.CatFastTests.class)
	void testFast() {
		System.out.println("fast");
		Assertions.assertTrue(true);
	}
	
	@Test
	@Category(com.example.junitmockitopowermock.categories.CatMediumTests.class)
	void tesfMedium() {
		System.out.println("medium");
		Assertions.assertTrue(true);
	}
	
}
