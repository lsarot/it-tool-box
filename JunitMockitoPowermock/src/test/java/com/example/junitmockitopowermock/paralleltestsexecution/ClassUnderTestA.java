package com.example.junitmockitopowermock.paralleltestsexecution;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


//EL RUNWITH() lo usamos en otra clase para usar JUnit ParallelComputer
//o podemos usar otro runner aquí, pero no el que llama al ParallelComputer pq hará un llamado recursivo!
public class ClassUnderTestA {
	
	
		//DESCOMENTO EL RUNWITH Y COMENTO ESTO
		//ES UN COMODÍN, PERO UNA MAMARRACHADA!
		/*@Test
	    public void runAllTests() {
	        Class<?>[] classes = { ParallelTestExecutionInner.class };

	        // ParallelComputer(true,true) will run all classes and methods 
	        // in parallel.  (First arg for classes, second arg for methods)
	        JUnitCore.runClasses(new ParallelComputer(true, true), classes);
	    }*/
	
	
		
		//@Category()
		@Test
		public void testParallelOne() {
			System.out.println("test parallel 1");
			try {Thread.sleep(3000);} catch (InterruptedException e) {System.out.println("interrupted");}
			Assert.assertTrue(true);
		}
		
		@Test
		public void testParallelTwo() {
			System.out.println("test parallel 2");
			try {Thread.sleep(3000);} catch (InterruptedException e) {System.out.println("interrupted");}
			Assert.assertTrue(true);
		}
		
		
				//la interna debe ser static. Para usar JUnit ParallelComputer con nuestro custom Runner
				//para  ejecutar directo desde aquí, JUnit 4 no lo hace, JUnit 5 si usamos @Nested
				//si nuestro custom Runner lo hace por reflexión, secuencialmente, podemos usar static o no depende de como se haya programado!
				@Category({com.example.junitmockitopowermock.categories.CatSlowTests.class})
				public static class ClassUnderTestA_Inner {
					
					@Test
					public void testParallelOne_1() {
						System.out.println("test parallel 3");
						try {Thread.sleep(3000);} catch (InterruptedException e) {System.out.println("interrupted");}
						Assert.assertTrue(false);
					}
					
					@Test
					public void testParallelTwo_1() {
						System.out.println("test parallel 4");
						try {Thread.sleep(3000);} catch (InterruptedException e) {System.out.println("interrupted");}
						Assert.assertTrue(true);
					}
					
				}
				
		
}
