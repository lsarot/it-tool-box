package com.example.junitmockitopowermock.powermock;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.junitmockitopowermock.mockito.login.UserForm;

/*
 * PowerMockito is a PowerMock's extension API to support Mockito. 
 * It provides capabilities to work with the Java Reflection API in a simple way 
 * to overcome the problems of Mockito, such as the lack of 
 * ability to mock final, static or private methods.
 */

////PARA MOCKEAR MÉTODOS STATIC, FINAL OR PRIVATE SE PUEDE USAR LIBRERÍA POWERMOCK JUNTO A MOCKITO... PERO LO IDEAL ES DESARROLLAR EL CÓDIGO FÁCIL DE TESTEAR

/* WHAT ARE MOCKS AND WHY DO WE NEED THEM ?
 * 
This paragraph is intentionally kept short and you can safely skip it in case you already know the concepts behind mocking.
In unit testing we want to test methods of one class in isolation. But classes are not isolated. They are using services and methods from other classes. Those are often referred to as collaborators. This leads to two major problems:
External services might simply not work in a unit testing environment as they require database access or are using some other external systems.
Testing should be focused on the implementation of one class. If external classes are used directly their behaviour is influencing those tests. That is usually not wanted.
This is when mocks are entering the stage and thus Mockito and PowerMock. Both tools are “hiding away” the collaborators in the class under test replacing them with mock objects. The division of work between the two is that Mockito is kind of good for all the standard cases while PowerMock is needed for the harder cases. That includes for example mocking static and private methods. 
*/

/* TO CONSIDER WHEN USING POWERMOCK --> mocking of static methods. But it is also possible to mock private methods and constructor calls.
 
PowerMock basically is a cry for help. It says "the class under test is badly designed, please fix it". Meaning: as a developer, you can write "easy to test" code, or "hard to test" code. Many people do the second: they write code that is hard to test. And then, PowerMock(ito) provides means to still test that code.
PowerMock(ito) gives you the ability to mock (thus control) calls to static methods, and to new(). To enable that, PowerMock(ito) manipulates the byte code of your code under test. That is perfectly fine for small code bases, but when you face millions of lines of production code, and thousands of unit tests, things are totally different.
I have seen many PowerMock tests fail for no apparent reason, to find out hours later ... that some "static" thing somewhere else was changed, and that somehow affect a different PowerMock static/new driven test case.
At some point, our team made a conscious decision: when you write new code, and you can only test that with PowerMock ... that isn't acceptable. Since then, we only created Mockito test cases, and not once since then we saw similar bizarre problems that bugged us with PowerMock.
The only acceptable reason to use PowerMock is when you want to test existing (maybe 3rd party) code that you do not want to modify. But of course, what is the point of testing such code? When you can't modify that code, why should tests fail all of a sudden? 
*/


//NO SE REQUIERE CREAR OTRA TEST-CONFIGURATION PARA JUNIT 4 (aquella del IDE para ejecutar o debuguear), FUNCIONA CON LA DEL 5 POR TENER EL VINTAGE

@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class) podemos delegar a otro runner los tests!, NO SÉ EXACTAMENTE QUÉ HACE
@PrepareForTest({SystemUnderTestPowerMock.class, MyStaticMethodsClass.class}) //fullyQualifiedNames = "com.baeldung.powermockito.introduction.*")
public class PowerMockTests {
	
	
	//POWERMOCK API FOR MOCKITO
	//POWERMOCKITO, TRAE MÉTODOS CON MISMO NOMBRE QUE EN MOCKITO
	//ASÍ PODEMOS LLAMAR A UNO U OTRO SIN MAYOR DIFERENCIA AL PARECER
	
	
	@Test
	public void testWithPowermock_PublicAndPrivateMethods() throws Exception {
		
		//PUBLIC & PRIVATE METHOD MOCKEADOS
		
		//
        // Given
        //
		UserForm userForm = new UserForm("****", "username");
		
		//prepare class for mocking static methods
		PowerMockito.mockStatic(MyStaticMethodsClass.class);
		//set mock behaviour
		PowerMockito.when(MyStaticMethodsClass.doLogin(userForm)).thenReturn("TEST");
		PowerMockito.when(MyStaticMethodsClass.callMyFinalMethod()).thenReturn(20);
		
        PowerMockito.mockStatic(SystemUnderTestPowerMock.class);     
        PowerMockito.when(SystemUnderTestPowerMock.callMyStaticMethod(false)).thenReturn(true);
 
        //
        // When
        //
        boolean b = new SystemUnderTestPowerMock().login(userForm);
 
        //
        // Then
        //
        //THE ASSERTION STEP IS QUITE STRANGE, IT REQUIRES TWO SEPARATE STEPS
        //one two declare times and other is like kind of call to that method. But has to be together.
        //The verifyStatic method must be called right before any static method verification for PowerMockito to know that the successive method invocation is what needs to be verified.
        //se debe usar verifyStatic(Clase.class, times(3)) y, luego llamamos a cualquier método static que queramos verificar que se llamó (1 sóla vez indiferente del número de veces que hayamos puesto en times!!!)
        PowerMockito.verifyStatic(MyStaticMethodsClass.class, Mockito.times(1));
        MyStaticMethodsClass.doLogin(userForm);//(Mockito.any());
        
        assertEquals(true, b);
        
        
        //PRIVATE METHOD (usamos un SPY)
        
        //USAMOS UN PUBLIC DESDE AQUÍ QUE LLAMARÁ A DICHO PRIVATE AL QUE LE TIENE ACCESO!
        SystemUnderTestPowerMock real = new SystemUnderTestPowerMock();
        SystemUnderTestPowerMock mock = Mockito.spy(real);
        
        PowerMockito.when(mock, "callPrivateMethod").thenReturn(true);
        boolean returnValue = mock.privateMethodCaller();
        
        PowerMockito.verifyPrivate(mock, Mockito.times(1)).invoke("callPrivateMethod");
        mock.privateMethodCaller();
        assertEquals(true, returnValue);
	}

	@Test
	public void testWithPowerMock_ConstructorAndFinalMethods() throws Exception {
		
		//CONSTRUCTOR & FINAL METHOD MOCKEADOS
		
		//First, we create a mock object using the PowerMockito API:
		SystemUnderTestPowerMock mock = PowerMockito.mock(SystemUnderTestPowerMock.class);

		//Next, set an expectation telling that whenever the no-arg constructor of that class is invoked, a mock instance should be returned rather than a real one:
		PowerMockito.whenNew(SystemUnderTestPowerMock.class).withNoArguments().thenReturn(mock);

		//Let's see how this construction mocking works in action by instantiating the SystemUnderTestPowerMock class using its default constructor, and then verify the behaviors of PowerMock:
		SystemUnderTestPowerMock collaborator = new SystemUnderTestPowerMock();
		PowerMockito.verifyNew(SystemUnderTestPowerMock.class).withNoArguments();

		//------
		
		//In the next step, an expectation is set to the final method:
		PowerMockito.when(collaborator.helloMethod()).thenReturn("Hello Baeldung!");

		//This method is then executed:
		String welcome = collaborator.helloMethod();

		//The following assertions confirm that the helloMethod method has been called on the collaborator object, and returns the value set by the mocking expectation:
		Mockito.verify(collaborator).helloMethod();
		assertEquals("Hello Baeldung!", welcome);
	}
	
	@Test(expected = RuntimeException.class)
	public void givenStaticMethods_whenUsingPowerMockito_thenCorrect() {
		PowerMockito
			.doThrow(new RuntimeException())
			.when(MyStaticMethodsClass.class);//aplica a toda la clase!
		
	    MyStaticMethodsClass.callMyFinalMethod();
	}
	
}
