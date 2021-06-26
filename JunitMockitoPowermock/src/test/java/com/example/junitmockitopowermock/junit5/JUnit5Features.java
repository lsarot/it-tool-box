package com.example.junitmockitopowermock.junit5;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.lightning.customthreadpool.LightningThreadPoolBuilder;
import com.lightning.customthreadpool.LightningThreadPoolBuilder.LightningThreadPool;


	// AAA: 3 PASOS EN TODOS LOS TEST

	// ARRANGE: setup your tests
	// ACT: execute what is under test
	// ASSERT: assertions
	
	// O TAMBIÉN WHEN - GIVEN - THEN

	
	//// https://junit.org/junit5/docs/current/user-guide/


	////JUnit 5 introduce varias mejoras    (https://www.paradigmadigital.com/dev/nos-espera-junit-5/)
	
	//// ALGUNAS FEATURES SON PROBADAS EN TESTS CON MOCKITO EN LoginControllerTests
	// ojo: usar org.junit.jupiter para el 5 y requiere JDK8+
	/**
	 * algo simple, los métodos de test pueden ser protected y friendly ahora, así evitar ser ejecutados desde fuera del contexto de JUnit
	 * JUnit ya no es una única biblioteca, sino que es un conjunto de tres subproyectos: 
	 * 1. JUnit Platform, 		permite el lanzamiento de los frameworks de prueba en la JVM. 
	 * 				(junit-platform-surefire-provider) (maven artifactId)
	 * 2. JUnit Jupiter, 			utilizar el nuevo modelo de programación (lambda) para la escritura de los nuevos tests de JUnit 5. 
	 * 				(junit-jupiter-engine)
	 * 3. JUnit Vintage, 			encargado de los tests de Junit 3 y 4. 
	 * 				(junit-vintage-engine)
	 * también usar dependencia 	
	 * 				junit-jupiter-api		si queremos sobreescribir la versión del API integrada en el engine
	 * 				junit							si queremos sobreescribir versión del API integrada en el engine del vintage
	 * y maven plugin 		maven-surefire-plugin   si usamos    junit-platform-surefire-provider
	 * 
	 * Features:
	 * Tags y customized Tags,			 para ejecutar un/unos grupo/s de pruebas solamente
	 * @DisplayName,		Nombrar tests, no recomendado realmente
	 * AssertThrows, 		AssertThrows recupera un Throwable
	 * MultipleAssertions, 		permite varios asserts y si falla uno igual comprueba el resto
	 * TimeOutAssertion
	 * @Disabled, 		en lugar de @Ignore
	 * @Nested, 		InnerClass Tests, así muestra más orden en los resultados.. limitación es que no podemos usar @BeforeAll ni @AfterAll en estas.
	 * AsummeTrue y assumingThat, 		ejecuta otra parte del código en caso de fallar la asserción
	 * @BeforeEach, @AfterEach, @BeforeAll, @AfterAll,		 que sustituyen a las anotaciones de JUnit 4 @Before, @After, @BeforeClass, @AfterClass
	 * Inyección de dependencias
	 * 			TestInfo: Podemos inyectar este objeto en un método de test, que dispone de datos sobre la propia ejecución del test. Y TestReporter
	 * 					.getDisplayName, .getTestMethod, etc
	 * @ExtendWith, 		indicando la clase que extiende la inyección.. y poder inyectar sobre un método test 		i.e. test1(@Mock Person persona)
	 * @TestFactory,		test se construyen en tiempo de ejecución
	 * ...
	 * */


@DisplayName("JUnit 5 FEATURES")
@TestInstance(Lifecycle.PER_METHOD)
//@RunWith(JUnitPlatform.class) 		JUnit4-based runner (si estamos usando JUnit5 en el proyecto, usamos esto para usar el JUnit4 si alguna clase de tests lo necesita).. ESTO NO HIZO FALTA EN TEST DE POWERMOCK PQ TIENE SU PROPIO RUNNER QUE ESTARÁ BASADO EN JUNIT 4 
public class JUnit5Features {
	
	
	@Tag("customTag")
	@DisplayName("Nombre personalizado del método")
	@Test
	void jUnit5Features() {

		assertAll("Try multiple things",
		       () -> assertTrue(true),
		       () -> assertEquals(1, 1),
		       () -> assertEquals(2, 2));

		assertTimeout(Duration.ofMillis(105), () -> Thread.sleep(100));
		//assertTimeout espera a que el código que se estaba comprobando termine, incluso nos indica por cuánto tiempo ha fallado el test. 
		//assertTimeoutPreemptively aborta el proceso una vez se excede el tiempo.
		
		boolean condition = true;
		assertTrue(condition == true, ()->"sólo se ejecuta si falla la condición, así no consume memoria del pool ;)");
		assumeTrue(true, () -> "Message that will be never shown");
		assumingThat(true, () -> assertEquals(1, 1, ()->"Se ejecuta si falla la condición"));
		
		//assertions with 3rd party libraries (comentado pq hay que agregarla)
		//import static org.hamcrest.CoreMatchers.equalTo; import static org.hamcrest.CoreMatchers.is; import static org.hamcrest.MatcherAssert.assertThat;
		//assertThat(2 + 1, is(equalTo(3))); librería de hamcrest
	}
	
	
	//Test Dinámicos JUnit 5
	@TestFactory
	Collection<DynamicTest> dynamicTestWithCollection() {
		//Los test dinámicos están formados por un nombre y una implementación de la interfaz funcional Executable
		return Arrays.asList(
	           dynamicTest("1st case", () -> assertTrue(true)),
	           dynamicTest("2nd case", () -> assertEquals(1, 1))
	   );
	}
	
	
	@TestFactory
	Stream<DynamicTest> givenNumberTestIfTheyArePair() {
	   return Stream
			   .of(2, 4, 6)
			   .map(
	           f -> 
	           dynamicTest("Given " +f+" test that it is pair", 
	        		   () -> assertEquals(0, f%2)));
	}
	
	
	private int random = new Random().nextInt(1000);
	
	@Test
	@DisplayName("Show Random 1")
	void test1() {
		System.out.println("\nRandom 1: " + random);
	}
	
	
	
	
	@DisplayName("TEST INNER CLASS")
	@Nested
	//@TestInstance(Lifecycle.PER_CLASS) //beforeEach, afterEach de la outer class aplican a estos también, excepto beforeAll y afterAll, a menos que usemos PER_CLASS
	class InnerTestClass {
		@Test
		@DisplayName("Show Random 2")
		void test2() {
			System.out.println("\nRandom 2: " + random);
		}
		@Test
		@DisplayName("Show Random 3")
		public void test3() {
			System.out.println("\nRandom 3: " + random);
		}
	}
	
	
    //---------------------------------------------------------------------------------------------
    
	
    /* USING SPRING FRAMEWORK TESTS

    @ExtendWith(SpringExtension.class) 		SpringExtension.class is provided by Spring 5 and integrates the Spring TestContext Framework into JUnit 5.
    		//Parece ser para poder usar anotaciones de Spring en JUnit 5 test, i.e. @Autowired para inyectar una dependencia, @MockBean, @Context y así
    		//If we want to migrate JUnit4 test to JUnit5 we need to replace the @RunWith annotation with the new @ExtendWith
    		//The @ExtendWith annotation accepts any class that implements the Extension interface.
    @RunWith(SpringJUnit4ClassRunner.class)		Parece ser análogo a @ExtendWith(SpringExtension.class) pero para integrar contexto Spring en JUnit 4
    		SpringJUnit4ClassRunner, which initializes the ApplicationContext needed for Spring integration testing—just like an ApplicationContext is created when a Spring application starts
    		Se puede usar cuando tenemos por ejemplo un Rest-Controller que queremos ejecutar pero no llamando a su método sino simulando una llamada real desde atrás para que pase por los filtros y demás.
    		Ejemplo:
    		MockMvc mockMvc = standaloneSetup(areaController).build();
    		mockMvc.perform(
            	MockMvcRequestBuilders.get("/api/area?type=RECTANGLE&param1=5&param2=4")
	        )
	        .andExpect(status().isOk())
	        .andExpect(content().string("20.0"));
    		
    */
    
    //When using @MockBean, the mock will replace any existing bean of the same type in the application context. (COMO SINGLETON GESTIONADO POR EL CONTENEDOR)
    //If no bean of the same type is defined, a new one will be added. This annotation is useful in integration tests where a particular bean – for example, an external service – needs to be mocked.
    //To use this annotation, we have to use SpringRunner to run the test
			//@ExtendWith(SpringExtension.class) SPRING 5 CON JUNIT 5
			//@RunWith(SpringJUnit4ClassRunner.class) SPRING 5 CON JUNIT 4
			//@RunWith(SpringRunner.class) SPRING 4 CON JUNIT 4
    /*
    @MockBean
    UserRepository mockRepository;
     
    @Autowired
    ApplicationContext context;
     
    @Test
    public void givenCountMethodMocked_WhenCountInvoked_ThenMockValueReturned() {
        
        Mockito.when(mockRepository.count()).thenReturn(123L);
 
        UserRepository userRepoFromContext = context.getBean(UserRepository.class);
        long userCount = userRepoFromContext.count();
 
        Assert.assertEquals(123L, userCount);
        Mockito.verify(mockRepository).count();
        
    }
    */
	
	
	//---------------------------------------------------------------------------------------------
	
	
	/* Atomic variables permite que múltiples threads accedan a la variable y la modifiquen sin generer problemas
	 * pero no usa un bloque synchronized sobre dicha variable wrapped en cada método, que pudiera!
	 * más bien usa un volatile type (private volatile int myInt)
	 * que garantiza que se lea de memoria ram y no de cache del cpu
	 * SOBRE volatile type:
	 * pudiera no ser suficiente usar variables volatile si el computador tiene varios CPU ya que asigna un thread a cada cpu y tienen cachés distintas.
	 * usamos entonces bloques synchronized
	 * los synchronized bloquean los threads, volatile no.
	 * usamos synchronized en casos más críticos entonces!
	 * */
	private static AtomicInteger t = new AtomicInteger(0);//si usamos un int normal, 2 threads toman su valor (10) y suman 1 (11), entonces nunca llega a 12 por ejemplo
	
	@DisplayName("CUSTOM THREAD POOL")
	@Test
	protected void testCustomThreadPool() throws IOException, InterruptedException {
		
		long totalTestTime = System.currentTimeMillis();
		int poolMaxSize = 15;
		int poolMinSize = 3;
		int taskExecutionTimeMillis = 500;
		
		System.err.println("\nWill run several tasks. Each waits " + taskExecutionTimeMillis + " ms");
		System.err.println("\n--> Using " + poolMinSize + " pool min threads!");
		System.err.println("--> Using " + poolMaxSize + " pool max threads!");
		
		long init = System.currentTimeMillis();

		////
		////COMPARAMOS CON UNA IMPL DE LA LIB DEL JDK java.util.concurrent
		//// 
		
		//ExecutorService exec = Executors.newFixedThreadPool(20); //MANTIENE N THREADS SIEMPRE ACTIVOS
		//ExecutorService exec = Executors.newCachedThreadPool();//NO SETEAS CANT INICIAL NI MAXIMA, pero aumenta o reduce en función de la demanda
		//ScheduledExecutorService exec = Executors.newScheduledThreadPool(20);//PUEDO PROGRAMAR TAREAS FRECUENTES O CON UN DELAY PARA SU EJECUCIÓN
		//LA IMPLEMENTACIÓN MÍA ES UNA COMBINACIÓN DE FIXED Y CACHED. PUEDO ESTABLECER MIN/MAX Y SE AUMENTA/REDUCE EL POOL AUTOMATICAMENTE!
		
		/*
		ExecutorService executorService = Executors.newFixedThreadPool(poolMaxSize);
		
	    for (int i = 0; i < 100; i++) {
	        executorService.execute(() -> {
	        	try {Thread.sleep(taskExecutionTimeMillis);} catch (InterruptedException e) {}
	        	
	            int currentT = t.incrementAndGet();
	           // if(currentT == 100)//imprimo el nro 100 para no llenar la consola
	            	//System.out.println(MessageFormat.format("t: {0}", currentT));
	        });
	    }
	    
	    executorService.shutdown();
		
	    do {} while (t.get() < 100);//como es async, esperamos que terminen todos

	    System.out.println("\n(Impl Java Concurrent lib) Elapsed time in ms: " + (System.currentTimeMillis()-init));
	    */
		
	    //------------------------ IMPL LEO
	    
	    t = new AtomicInteger(0);
	    
	    LightningThreadPool pool = LightningThreadPoolBuilder
				.createCustomPool()
				.minPoolSize(poolMinSize)
				.maxPoolSize(poolMaxSize)
				.build();
	    
	    System.out.println();
	    
	    //SE EJECUTAN 2 ASYNC
	    
	    System.out.println("Inician 2 async task! (each lasts 5sec)");
	    try {
	    	for (int i = 0; i < 2; i++) {
	    		pool.executeAsync(() -> {
	    			try {Thread.sleep(5000);} catch (InterruptedException e) {}
	    			System.out.println("Done with async task!");
	    		});
			}
		} catch (Exception e) {e.printStackTrace();}
	    
	    
	    //SE EJECUTAN 100 SYNC
	    //al iniciar, habrán 2 threads ocupados por las asíncronas, por lo que el marcador de tiempo se verá afectado pq deberá crear más threads!!!
	    
	    init = System.currentTimeMillis();
	    System.out.println("Inician 100 tasks síncronas! (each of " + taskExecutionTimeMillis + " ms)");
		for (int i = 0; i < 100; i++) {
			try {
				final int j = i;
				pool.executeAndWait(() -> {
					if (j == 30 || j == 70) {
						throw new RuntimeException("failure in task nbr "+j);
					}
					try {Thread.sleep(taskExecutionTimeMillis);} catch (InterruptedException e) {}
				});
				
			} catch (Exception e) {e.printStackTrace();}
		}
		System.out.println("\n(Impl Leo) Elapsed time in ms: " + (System.currentTimeMillis()-init));

		
		//SE EJECUTAN 50 SYNC
		
		init = System.currentTimeMillis();
	    System.out.println("\nInician 50 tasks síncronas! (each of " + taskExecutionTimeMillis + " ms)");
		for (int i = 0; i < 50; i++) {
			try {
				pool.executeAndWait(() -> {
					try {Thread.sleep(taskExecutionTimeMillis);} catch (InterruptedException e) {}
				});
			} catch (Exception e) {e.printStackTrace();}
		}
		System.out.println("\n(Impl Leo) Elapsed time in ms: " + (System.currentTimeMillis()-init));
		
		
		//DORMIMOS 15 SEG PARA VER QUE REDUCE THREADS DEL POOL
		System.out.println("\nEsperamos 15s para ver cómo reducen threads del pool.");
		System.out.println("--puede haber sido cambiado el valor elapsed_idle_time_to_shrink_pool a un tiempo diferente!");
		try {Thread.sleep(15000);} catch (InterruptedException e) {}
		
		
		//SE EJECUTAN 100 ASYNC
	    
		init = System.currentTimeMillis();
	    System.out.println("Inician 100 async task! (each of " + taskExecutionTimeMillis + " ms)");
		for (int i = 0; i < 100; i++) {
			try {
				pool.executeAsync(() -> {
					try {Thread.sleep(taskExecutionTimeMillis);} catch (InterruptedException e) {}
				});
			} catch (Exception e) {e.printStackTrace();}
		}
		System.out.println("\n(Impl Leo) Elapsed time in ms: " + (System.currentTimeMillis()-init));
		
		
		System.out.println();
		
		//FINISHING POOL (UNFORCED)
		System.out.println("*Finishing pool (unforced)*");
		pool.terminatePool(false);
		System.out.println("Waiting for all...");
		pool.waitForAll();
		
		//FINISHING POOL (FORCED)
		/*System.out.println("*Finishing pool (forced)*");
		pool.terminatePool(true);
		System.out.println("Waiting for all...");
		pool.waitForAll();*/
		
		System.out.println("Finished!");
		System.out.println("Total test time in ms: " + (System.currentTimeMillis()-totalTestTime));
	}
	
	

}
