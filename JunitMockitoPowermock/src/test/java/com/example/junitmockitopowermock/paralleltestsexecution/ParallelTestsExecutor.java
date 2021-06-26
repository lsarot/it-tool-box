package com.example.junitmockitopowermock.paralleltestsexecution;

import org.junit.runner.RunWith;

import com.example.junitmockitopowermock.customrunners.ParallelBlockingRunner;
import com.example.junitmockitopowermock.customrunners.ParallelRunnerDefault;
import com.example.junitmockitopowermock.customrunners.ParallelRunnerFromParent;
import com.example.junitmockitopowermock.paralleltestsexecution.ClassUnderTestA.ClassUnderTestA_Inner;


/** MUESTRA USO DE UN CUSTOM RUNNER PROPIO.
 * ESTE RECUPERA CLASES A TESTEAR Y CATEGORÍAS (APLICAN SOBRE CLASES SOLAMENTE)
 * Y EJECUTA DICHAS CLASES SI ESTÁN EN DICHAS CATEGORÍAS
 * 
 * Si uno falla no continúa :(
 * No muestra detallado por test-method (el Runner que usamos)
 * ESTÁ MEJOR IMPL EL DE TERCERO (en ParallelTestExecutor2)
 * 
 * MUESTRA USO DE JUNIT ParallelComputer PARA EJECUTAR EN PARALLELO LOS TESTS DE DICHAS CLASES
 * Y OTRAS ALTERNATIVAS SE MENCIONAN EN EL CÓDIGO DEL RUNNER
 * 
 * LA IDEA ES TENER UNA CLASE CENTRAL DONDE DECIDIR QUÉ CLASES A TESTEAR Y CATEGORÍAS
 * 		PARA ESO USO MI RUNNER PERO CON EL LLAMADO POR REFLECTION
 * 		O TESTS PARALELOS, DEPENDE DE LO QUE SE QUIERA EN EL PROYECTO
 * */


//@RunWith(JUnit4.class)//DEFAULT, no hace falta declarar su uso
//ESTOS 3 SON CUSTOM RUNNERS:
		@RunWith(ParallelRunnerDefault.class)
//@RunWith(ParallelRunnerFromParent.class)
//@RunWith(ParallelBlockingRunner.class)
public class ParallelTestsExecutor {

	/**
	 * Ejecuto los tests de estas clases (en paralelo)
	 * */
	public Class<?>[] getTestClassesToRun() {
		return new Class[] 
		{
			com.example.junitmockitopowermock.paralleltestsexecution.ClassUnderTestA.class,		
			com.example.junitmockitopowermock.paralleltestsexecution.ClassUnderTestA.ClassUnderTestA_Inner.class		
		};
	}
	
	/**
	 * Ejecuto TODOS los test-methods de la clase que incluya @Category({}) en su firma
	 * No discrimina por método, sino toda la clase.
	 * Si está vacío el vector o es null, ejecuta todas las clases!
	 * */
	public Class<?>[] getTestCategoriesToRunUponTestClassesToRun() {
		return new Class[]
		{
			com.example.junitmockitopowermock.categories.CatSlowTests.class,
			com.example.junitmockitopowermock.categories.CatSlowerTests.class
		};
	}
	
}
