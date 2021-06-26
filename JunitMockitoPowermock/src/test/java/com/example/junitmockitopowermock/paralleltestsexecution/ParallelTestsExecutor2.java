package com.example.junitmockitopowermock.paralleltestsexecution;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.example.junitmockitopowermock.customrunners.ParallelSuite;
import com.example.junitmockitopowermock.mockito.login.LoginControllerTests;
import com.example.junitmockitopowermock.paralleltestsexecution.ClassUnderTestA.ClassUnderTestA_Inner;
import com.example.junitmockitopowermock.powermock.PowerMockTests;


/** MUESTRA USO DE UN CUSTOM RUNNER DE OTRO.
 * Este no trabaja con Categorías, y modificarlo está difícil.
 * PERO trabaja como una Suite, le dices qué clases quieres ejecutar.
 * Podemos hacer una suite por categoría.
 */

@RunWith(ParallelSuite.class) // O ParallelParameterized.class Suite.class las ejecutará secuencial
@SuiteClasses({
	ClassUnderTestA.class, 
	ClassUnderTestA_Inner.class,
	LoginControllerTests.class,//requieren usar un @RunWith para que funcione con esta estructura !, PODEMOS COMENTAR QUE SE EJECUTE APARTE! pq no notifica.
	PowerMockTests.class
	})
public class ParallelTestsExecutor2 {

	//con ParallelSuite, ejecuta clases en paralelo, no a nivel de métodos!
	//Los demás es lo mismo, ParallelComputer de JUnit, librería que ejecuta en dif. ficheros, y todas las que llamen a Result result = JUnitCore.runClasses(...     
	
	//PUEDO METER EN SuiteClasses las clases que cumplean con una categoría
	//y hacer uno de estos para cada Suite que quiera ejecutar en paralelo
	
	
	//ParallelParameterized requiere un método public static parameters que no sé qué es!
	
}
