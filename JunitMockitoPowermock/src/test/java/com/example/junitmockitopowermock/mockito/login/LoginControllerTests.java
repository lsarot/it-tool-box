package com.example.junitmockitopowermock.mockito.login;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@TestInstance(Lifecycle.PER_CLASS) //los @Mock (mocks a nivel de clase) serán la misma instancia para cada test method, en lugar de una instancia dif por cada método!
		//usar PER_CLASS dará errores al usar un assert que verifique nro de llamadas a un método de dicha instancia, pq pudo haberse ejecutado antes en otro método!, pero lo resolvemos abajo instanciando en BeforeEach o en cada método en sí
//@TestInstance(Lifecycle.PER_METHOD)//debe declararse static @BeforeAll y @AfterAll methods
//@RunWith(MockitoJUnitRunner.class) //colocamos esto para activar las Mockito annotations o, usamos MockitoAnnotations.initMocks(this) en método @BeforeAll
public class LoginControllerTests {
	
	
	//// LoginController 
			//usa LoginService (LoginService mantiene usuario actual en variable currentUser)
					//usa LoginDao 
							//usa UserForm(bean)
	
	
	@Mock
    //@Mock makes it easier to find the problem mock in case of a failure, as the name of the field appears in the failure message
    //compared to using Mockito.mock(x.class) (que retorna una instancia mockeada)
    private LoginDao loginDao;
 
    @Mock
    private LoginService loginService;
    
    @Mock
    private LoginService loginService0;

    @Spy //tipo de mock donde se usará la implementación real de los métodos no mockeados
    @InjectMocks //porque requiere de un LoginDao para funcionar si lo usamos.. no se puede usar @Mock e @InjectMocks a la vez
    //Un tipo Spy no se inyectará nunca, debemos asignarlo a la variable, con un setter, constructor, etc.
    //Para injectar un objeto, se debe usar el mismo nombre que tiene en la clase.. por eso nunca habrá conflicto con otro del mismo tipo. 
    private LoginService spiedLoginService;
    
    @InjectMocks
    private LoginController loginController;
 
    
    //-------------------------------------------
    
    
    @BeforeAll
    void setUp() {
        loginController = new LoginController();
        MockitoAnnotations.initMocks(this);
    }
    
    @AfterAll
    void bye() {
    	System.out.println("ThankYou!");
    }
    
    
    //-------------------------------------------
    
    @BeforeEach
    void init(TestInfo tinfo) {
    	System.out.println("Begin: " + tinfo.getTestClass() + " . " + tinfo.getTestMethod());
    	
    	//al setear behavior a un mock ya no cambia al parecer, entonces instanciamos un nuevo mock dentro del método
    	//o reinstanciamos el global en @BeforeEach
    	//a menos que usemos @TestInstance(Lifecycle.PER_METHOD) a nivel de clase
    	loginService0 = Mockito.mock(LoginService.class);
    }
    
    @AfterEach
    void end(TestInfo tinfo) {
    	System.out.println("End: " + tinfo.getTestClass() + " . " + tinfo.getTestMethod());
    }
    
    //-------------------------------------------
    
    
    //métodos que muestran quién los llamó, así marcamos inicio y cierre de un método por cuestiones de trazas.
    private void showCallerBegin() {
    	StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
    	StackTraceElement e = stacktrace[2];
    	String methodName = e.getMethodName();
    	
    	System.out.println("Begin: " + methodName);
    }
    
    private void showCallerEnd() {
    	StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
    	StackTraceElement e = stacktrace[2];
    	String methodName = e.getMethodName();
    	
    	System.out.println("End: " + methodName);
    }
    
    
    //-------------------------------------------
    
    
    @Test
    protected void assertThatNoMethodHasBeenCalled() {//Alt+Shift+X, T   shortcut for executing a test
    	showCallerBegin();
    	
        loginController.login(null);
        
        Mockito.verify(loginService, Mockito.never()).login(Mockito.any());
        Mockito.verify(loginService, Mockito.times(0)).login(null);
        //Mockito.verify(loginService).login(null);//equivalent to times(1)
        
        Mockito.never();//no interaction with none of the mocks
        Mockito.verifyNoInteractions(loginService);//no interaction with given mocks
        Mockito.verifyNoMoreInteractions(loginService);//test that there were no more calls to the given Mocks, but at which point ?, it seems to be same as Mockito.verifyNoInteractions
        
        showCallerEnd();
    }
    
    @Test
    protected void assertTwoMethodsHaveBeenCalled() {

    	// ARRANGE
    	
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        
        //creamos un Mock local pq el global que está configurado para ser el mismo para todos los métodos aparentemente no se puede confgurar un método 2 veces, o me estaba dando fallo al verificar el valor retornado
        //LoginService loginService0 = Mockito.mock(LoginService.class);
        //fue instanciado en @BeforeEach
        Mockito.when(loginService0.login(userForm)).thenReturn(true);
        
        loginController.setLoginService(loginService0);

        //Mockito.when(loginService.login(userForm)).thenReturn(true);//aparentemente no le hace caso a esto, debe ser porque ya se seteó su comportamiento anteriormente en otro método.
        //Mockito.when(loginService.login(userForm)).thenReturn(true,false);//we can pass many arguments for consecutive method calls!
     
        // ACT
        
        String login = loginController.login(userForm);

        // ASSERT
        
        Assertions.assertEquals("OK", login);
        Mockito.verify(loginService0).login(userForm);
        Mockito.verify(loginService0).setCurrentUser("foo");
    }
     
    @Test
    protected void assertOnlyOneMethodHasBeenCalled() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        Mockito.when(loginService.login(userForm)).thenReturn(false);
     
        String login = loginController.login(userForm);
     
        Assertions.assertEquals("KO", login);
        Mockito.verify(loginService, Mockito.times(0)).login(userForm);
        //Mockito.verify(loginService).login(Mockito.any()); falla como que nunca se hubiera invocado ni 1 vez!!!
        Mockito.verifyNoMoreInteractions(loginService);
    }
    
    @Test
    protected void mockExceptionThrowin() {
        UserForm userForm = new UserForm();
        Mockito.when(loginService.login(userForm)).thenThrow(IllegalArgumentException.class);
     
        String login = loginController.login(userForm);
     
        Assertions.assertEquals("ERROR", login);
        Mockito.verify(loginService).login(userForm);
        //Mockito.verifyNoInteractions(loginService); will fail
    }
    
    @Test
    protected void mockAnObjectToPassAround() {
    	//se le pasa un objeto mockeado (pero dentro de este método), y no a nivel de clase como i.e. loginService
        UserForm userForm = Mockito.when(Mockito.mock(UserForm.class).getUsername())
          .thenReturn("foo").getMock();//se le pasa un mock anónimo, pero luego se puede recuperar con getMock()
        
        //LoginService loginService0 = Mockito.mock(LoginService.class);
        Mockito.when(loginService0.login(userForm)).thenReturn(true);
     
        loginController.setLoginService(loginService0);
        
        String login = loginController.login(userForm);
     
        Assertions.assertEquals("OK", login);
        Mockito.verify(loginService0).login(userForm);
        Mockito.verify(loginService0).setCurrentUser("foo");
    }
    
    @Test
    protected void argumentMatching() {
    	//Sometimes argument matching for mocked calls needs to be a little more complex than just a fixed value or anyString().
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        // default matcher
        Mockito.when(loginService.login(Mockito.any(UserForm.class))).thenReturn(true);
     
        String login = loginController.login(userForm);
     
        Assertions.assertEquals("OK", login);
        Mockito.verify(loginService).login(userForm);
        
        // complex matcher (WHAT WE'RE TRYING TO SHOW)
        Mockito.verify(loginService).setCurrentUser(ArgumentMatchers.argThat(
            new ArgumentMatcher<String>() {
                @Override
                public boolean matches(String argument) {
                    return argument.startsWith("foo");
                }
            }
        ));
    }
    
    @Test
    protected void partialMocking() {
    	////GIVEN
    	
    	//Mockito allows partial mocking (a mock that uses the real implementation instead of mocked method calls in some of its methods) in two ways.
    	//1-You can create a spy instead of a mock in which case the default behavior for that will be to call the real implementation in all non-mocked methods.
    	//2-You can either use .thenCallRealMethod() in a normal mock method call definition
    	
        // 1- use partial mock
    	//el Spy nunca se inyectará, debemos setearlo como dé lugar!
        //loginController.loginService = spiedLoginService;//asignamos al miembro público el spied-Mock en vez del inyectado que es el normal-Mock
        loginController.setLoginService(spiedLoginService);
    	
        //otra manera de instanciar el Spy
        //LoginService lsSpy = Mockito.spy(LoginService.class);
        
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        
        // let service's login use implementation (la implementación y no el mockeo) so let's mock DAO call..
        //mockeamos el Dao pq se usará la implementación real de loginService
        Mockito.when(loginDao.login(userForm)).thenReturn(1);
        
        //*** LA FORMA CORRECTA DE MOCKEAR MÉTODO DE UN @SPY (si no queremos usar la impl real)
        //Mockito.when(spiedLoginService.login(userForm)).thenReturn(true); //ASÍ NO!
        //Mockito.doReturn(true).when(spiedLoginService).login(userForm); //Así!
        
        
        ////WHEN
        String login = loginController.login(userForm);
        
        
        ////THEN
        Assertions.assertEquals("OK", login);
        // verify mocked call
        Mockito.verify(spiedLoginService).login(userForm); //se verifica el llamado ya sea a la implementación real o al mockeo!
        Mockito.verify(spiedLoginService).setCurrentUser("foo");
        
        
        //--------------- USANDO		 thenCallRealMethod 
        //si tenemos un mock normal, todos los métodos harán doNothing a menos que los mockiemos, con .thenCallRealMethod llamamos al real
        //si tenemos un mock spy, todos los métodos harán el llamado real a menos que los mockiemos.
        
        
        ////GIVEN
        LoginService loginService0 = Mockito.mock(LoginService.class);
        loginController.setLoginService(loginService0);
        Mockito.when(loginService0.login(userForm)).thenCallRealMethod();
        //al ser un Mock, no puedo setearle a la instancia el loginDao ni inyectarsela por ser Mock justamente,
        		//**pudiera pasar el Dao por parámetro/argumento al método .login
        //en cambio spiedLoginService sí se le puede inyectar Mocks, por eso sí tiene el loginDao asignado!
        		//and set behavior as Mockito.doReturn(1).when(spyLoginDao).login(userForm);
        
        ////WHEN
        login = loginController.login(userForm);
        
        ////THEN
        Assertions.assertEquals("KO", login);
    }
    
    @Test
    void manipulatingObjectsPassedToMockedObjectsAsParameters() {
    	// 
        // Given
        //
    	UserForm userForm = Mockito.when(Mockito.mock(UserForm.class).getUsername())
    	          .thenReturn("foo").getMock();//se le pasa un mock anónimo, pero luego se puede recuperar con getMock()
    	        
    	Mockito.when(loginService0.login(userForm)).thenReturn(true);
    	     
    	
    				//cuando llame a loginService0.login con userForm, se llamará a este método!
			    	Mockito.doAnswer(new Answer<Boolean>() {
			    		@Override
			    		public Boolean answer(InvocationOnMock invocation) throws Throwable {
			    			UserForm originalObject = invocation.getArgument(0, UserForm.class);//(invocation.getArguments())[0];

			    			//puedo evaluar algo del objeto pasado como parámetro
			    			//y en base a eso devolver un valor u otro
			    			
			    			return false;
			    		}})
			    	.when(loginService0)
			    	.login(userForm);

			    	
    	loginController.setLoginService(loginService0);

		//
        // When
        //
        String login = loginController.login(userForm);
    	  
    	//
        // Then
        //
        Assertions.assertEquals("KO", login);
        Mockito.verify(loginService0).login(userForm);
        Mockito.verify(loginService0, Mockito.times(0)).setCurrentUser("foo");
    }
    
}