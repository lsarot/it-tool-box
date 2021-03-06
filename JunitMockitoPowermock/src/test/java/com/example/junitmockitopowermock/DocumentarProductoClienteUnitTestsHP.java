package com.example.junitmockitopowermock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;

import edu.emory.mathcs.backport.java.util.Arrays;
import giros.server.bd.AccesoBD;
import giros.server.bd.dao.core.CoreDAOImpl;
import giros.server.bd.dao.physis.DocumentarProductoClienteDaoImpl;
import giros.server.bd.dao.physis.SwitchUniparDAO;
import giros.server.bd.dao.trazasws.TrazaWsDao;
import giros.server.services.beans.ConstantesCobertura;
import giros.server.services.beans.physis.ConstantesProvision;
import giros.server.services.beans.physis.TipoClienteProvision;
import giros.server.services.beans.physis.documentarProductoCliente.DocumentarProductoClienteEntrada;
import giros.server.services.beans.physis.documentarProductoCliente.DocumentarProductoClienteSalida;
import giros.server.services.beans.physis.documentarProductoCliente.ProductoCliente;
import giros.server.services.impl.physis.DocumentarProductoClienteImpl;
import giros.server.services.impl.physis.ValidacionOperacion;
import giros.server.services.physis.DocumentarProductoCliente;
import giros.server.util.DatosThread;


@RunWith(PowerMockRunner.class)
@PrepareForTest({
	DocumentarProductoClienteDaoImpl.class,
	AccesoBD.class,
	TrazaWsDao.class,
	ValidacionOperacion.class,
	SwitchUniparDAO.class,
	DatosThread.class,
	TrazaWsDao.class,
	DocumentarProductoCliente.class //aunque no creemos un mock, queremos mockear un constructor al que llama en un Test
	})
public class DocumentarProductoClienteUnitTestsHP {

	//https://github.com/powermock/powermock/wiki/Getting-Started
	//Need to combine PowerMock with another JUnit runner?
	//https://github.com/powermock/powermock/wiki/PowerMockAgent
	/*Since version 1.4.9 it's possible to bootstrap PowerMock using a Java agent 
	 * instead of using the PowerMockRunner and the RunWith annotation. 
	 * This allows you to use e.g. other JUnit runners while still benefiting from PowerMock's functionality.
	 * The main difference between the agent based bootstrapper and the classloading based bootstrapper is that
	 * you don't run into classloading issues when using XML frameworks etc. It's recommended to use this way of
	 * bootstrapping when using PowerMock for integration testing larger parts of a system.
	 */
	//NO FUNCION??!
	/*static {
		PowerMockAgent.initializeIfNeeded();
	}*/
	//@Rule
    //public PowerMockRule rule = new PowerMockRule();
	
	//-----------------------------------------------------

	private static final Logger logger = Logger.getLogger(TestDocumentarProductoClienteUnitTests.class);
	
	private static Map<String, Integer> availableOpsMap;
	
	private AccesoBD mockAccesoBD;
	
	//-----------------------------------------------------
	
	/**
	 * NING??N MOCKEO DEBE HACERSE AQU??, PUEDE AFECTAR AL PASAR A OTRO TEST METHOD!
	 * */
	@BeforeClass 
	public static void beforeAll() {
		//--- MAPA DE OPERACIONES DISPONIBLES
		//SI QUEREMOS UN MAP DE OPERACIONES DISTINTAS EN UN TEST, simplemente lo creamos en dicho test.. 
		fillGlobalAvailableOperationsMapWithValues();
		//si es espec??fico para un test, colocar esta instrucci??n justo antes del paso 3. ACTUAL EXECUTION (al final del paso 2.)
		DocumentarProductoCliente.setAvailableOperationsMap(availableOpsMap);//si no seteamos ops, intentar?? recuperar de BBDD

		//--- OTRO
		//...
		
	}
	

	@Before
    public void beforeEach() {
		try {
			//--- TODAS LAS CLASES CON M??TODOS STATIC QUE QUERAMOS MOCKEAR
		    PowerMockito.mockStatic(DocumentarProductoClienteDaoImpl.class);
			PowerMockito.mockStatic(AccesoBD.class);
			PowerMockito.mockStatic(TrazaWsDao.class);
			PowerMockito.mockStatic(ValidacionOperacion.class);
			PowerMockito.mockStatic(SwitchUniparDAO.class);
			PowerMockito.mockStatic(DatosThread.class);
			
			
			//--- MOCK DE ACCESOBD
			//***** NECESARIO PARA TODOS LOS TESTS, sino intentar?? acceder a la BBDD!!!
			//***** Si se requiere uno real, simplemente asignar/instanciar un new AccesoBD (NO MOCK) en el test particular (con el mismo nombre y asignado a la variable global o a una local)
			//***** Si se requiere otro comportamiento, setearlo en un NUEVO MOCK dentro del m??todo del test particular (se han observado anomal??as (no sirve) al mockear una segunda vez el m??todo del mock, por eso uno nuevo)
			
					/* SI USAMOS SPY (TIPO DE MOCK), EN LOS M??TODOS NO MOCKEADOS SE LLAMAR?? A LA IMPLEMENTACI??N REAL
						AccesoBD realAccesoBD = new AccesoBD();
						AccesoBD spymockAccesoBD = Mockito.spy(realAccesoBD);
						//Mockito.when(spymockAccesoBD.conectar()).thenReturn(true); //COMO ES SPY NO SE MOCKEA AS??!
						//PowerMockito.when(spymockAccesoBD.conectar()).thenReturn(true); //NI AS??
						Mockito.doReturn(true).when(spymockAccesoBD).conectar(); //SINO AS??
					 */
			
			// USAREMOS MOCK Y EN DADO CASO SETEAMOS QUE LLAME AL M??TODO REAL
			mockAccesoBD = Mockito.mock(AccesoBD.class);
			Mockito.when(mockAccesoBD.conectar())
				.thenReturn(true);
			DocumentarProductoCliente.setAccesoBD(mockAccesoBD);//para evitar acceder a BBDD en algunos puntos donde usa instancia y no m??todo static
			//Mockito.when(mockAccesoBD.conectar()).thenCallRealMethod(); //SI QUEREMOS QUE UN MOCK NORMAL (NO SPY) LLAME A LA IMPL REAL.
				//o PowerMockito.doCallRealMethod().when(...); si es static
			
			
			//--- OTRO
			//...
			
		} catch (Exception e) {
			Assert.fail("Error in beforeEach method!!!");//capturamos para que no afecte a todo el banco de tests!!!
		}
    }

	private static void fillGlobalAvailableOperationsMapWithValues() {
		availableOpsMap = new TreeMap<String, Integer>();
		availableOpsMap.put("BAJA_TEMPORAL", 1);
		availableOpsMap.put("BAJA_TEMPORAL_DATOS", 2);
		//...
	}
	
	
	@AfterClass
	public static void afterAll() {
		
	}
	
	
	@After
	public void afterEach() {
		
	}
	
	
	//-----------------------------------------------------
	
	
	@Test
	public void templateForAnyTest() {
		try {
		// ARRANGE
        // ------------------ GIVEN
        //
			
		//------------------- 1. PREPARING VARIABLES
		//...todo lo necesario para hacer la prueba
		
		//... <<<<< TRABAJAR AQU?? >>>>>
			
		//------------------- 2. DECLARING & SETTING MOCKS BEHAVIOR
		//...seteamos lo que devolver?? o har?? cada mock que nos interese durante el proceso
	
		//... <<<<< TRABAJAR AQU?? >>>>>
			
		// ACT
        // ------------------ WHEN
        //
		
		//------------------- 3. ACTUAL EXECUTION
		//...inicia el flujo de nuestro test (unitario o integraci??n)
		
		//... <<<<< TRABAJAR AQU?? >>>>>
			
		// ASSERT
        // ------------------ THEN
        //
		
		//------------------- 4. VARIFYING CALLS	
		//...se verifica si se llam?? o no a x m??todo, x nro de veces y otras cosas
			//no se debe verificar todo, s??lo lo que no abarque otros TESTS
			//habr??n UNIT-TESTS que validen i.e. que un m??todo que acceda a BBDD lo haga correctamente
		
		//... <<<<< TRABAJAR AQU?? >>>>>
			
		//------------------- 5. ASSERTIONS
		//...para las aserciones se necesitan datos de salida, en este caso disponemos de muy pocos
            //pero nos apoyamos verificando llamados a los m??todos durante la ejecuci??n		
		
		//... <<<<< TRABAJAR AQU?? >>>>>

		} catch (Exception e) {
			//muestra la exception y nombre del m??todo que fall??!
			e.printStackTrace();
			String msg = "TEST FAILED - " + new Object(){}.getClass().getEnclosingMethod().getName() + " -->\n" + e.getMessage();
			System.err.println(msg);
			Assert.fail(msg);//SIN ESTO, SE REPORTAR?? QUE LA PRUEBA NO TUVO ERRORES, _TODO OK
		}
	}
	
	
	//-----------------------------------------------------
	
	
	@Test
	public void baja_temporal() {
		try {
		// ARRANGE
        // ------------------ Given
        //
			
		//------------------- 1. PREPARING VARIABLES

		String operacion = ConstantesProvision.DOC_PROD_OPERACIONES.BAJA_TEMPORAL.toString(false);
		String telefono = "866000999";
		String idClienteExterno = "55220120";
		String sistema = "LEOSYSTEM";
			
		DocumentarProductoClienteEntrada datosEntrada = new DocumentarProductoClienteEntrada();
		DocumentarProductoClienteSalida datosSalida = new DocumentarProductoClienteSalida();

		// Tests baja temporal varios
		datosEntrada.setOperacion(operacion);
		datosEntrada.setSistema(sistema);
		
		// PRODUCTO
		ProductoCliente[] productos = new ProductoCliente[1];
		ProductoCliente producto1 = new ProductoCliente();

		// todas requieren estos campos de entrada
		producto1.setPdTelefono(telefono);
		producto1.setPdIdClienteExterno(idClienteExterno);
		
		productos[0] = producto1;
		
		datosEntrada.setProducto(productos);
		
		//algunos verify usar??n este valor para saber que se llam?? a x m??todo. Se toma el valor ahora pq luego se modificar?? ya que se pas?? por referencia!
		String datosEntradaInitialToString = datosEntrada.toString();
		
		//------------------- 2. DECLARING & SETTING MOCKS BEHAVIOR
		
				//m??todo .doNothing es default para void methods de mocks (tambi??n hace nada si no es void, pero devuelve null o default si es primitivo el return-type)
				//se puede usar para un spy mock para que no llame al m??todo impl
				//o en una cadena de comportamientos tipo .doNothing().doThrow(new RuntimeException()).when(mock).methodName() para que primero no haga nada y en segundo llamado arroje exception
				//PowerMockito.doNothing().when(DatosThread.class, "setAccesoBD", Mockito.any(AccesoBD.class));
				//PowerMockito.doNothing().when(DatosThread.class, "getAccesoBD");
				//PowerMockito.doNothing().when(AccesoBD.class, "cerrarConexionesAbiertas");
				//PowerMockito.doNothing().when(TrazaWsDao.class);

				//si usamos Matchers debe ser en todos los argumentos (Mockito.anyString()|Mockito.eq(""), etc.)
		
		/* SI LO SETEAMOS LOCAL PARA EL M??TODO (ya se hace global en beforeEach!!!)
		mockAccesoBD = Mockito.mock(AccesoBD.class);
		Mockito.when(mockAccesoBD.conectar())
			.thenReturn(true);
		//o mockAccesoBD = new AccesoBD(); //SI NO QUEREMOS QUE SEA MOCK!	
		*/
		
		PowerMockito.when(DatosThread.getAccesoBD())
			.thenReturn(mockAccesoBD);
		
		PowerMockito.when(SwitchUniparDAO.obtenerTipoOperacion(operacion))
			.thenReturn(ConstantesProvision.TIPO_OPERACION_GENERICO);
		
		PowerMockito.when(SwitchUniparDAO.estaSwitchActivo())
			.thenReturn(true);
		
		PowerMockito.when(SwitchUniparDAO.verificarSwitchGenerico(sistema, telefono, "", datosEntradaInitialToString))
			.thenReturn(Arrays.asList(new String[]{ConstantesProvision.SISTEMA_GIROS, "ID_TRAZA_FAKE"}));
		
		//SWITCH PHYSIS
		
		ArrayList<String> resultadoPhysis = new ArrayList<String>();
		resultadoPhysis.addAll(Arrays.asList(new String[] {"A","B","C","D","E","F","G","H","0",""}));
		
		PowerMockito.when(AccesoBD.ejecutarProcedimiento(ConstantesProvision.PL_HPSA_BAJA_TEMPORAL, Arrays.asList(new String[]{telefono, idClienteExterno})))
			.thenReturn(resultadoPhysis);
		
		//SWITCH GIROS

		String iua = "IUA123456789";
		
		PowerMockito.when(DocumentarProductoClienteDaoImpl.obtenerIUAPorTelefono(telefono))
			.thenReturn(iua);
		
		TipoClienteProvision tipoCli = new TipoClienteProvision();
		tipoCli.setCadena("CADENA");
		tipoCli.setModeloServicio("MOD_SERVICIO");
		tipoCli.setRecursoTransporte("RECURSO_TRANSPORTE");
		tipoCli.setTecnologia("TECNOLOGIA");
		tipoCli.setTelefonoFicticio("TEL_FICTICIO");
		tipoCli.setTipoCliente("TIPO_CLIENTE");
		tipoCli.setTipoRecurso("TIPO_RECURSO");
		tipoCli.setTipoServicioSiam("ADSL/VDSL");
		tipoCli.setVlanGGCC("VLAN_GGCC");
		tipoCli.setVula("VULA");
		tipoCli.setOperadorCliente("NOMBREOPECLI");
		tipoCli.setOperadorRed("NOMBREOPERED");
		tipoCli.setIdRecurso("ID_RECURSO");
		
		PowerMockito.when(DocumentarProductoClienteDaoImpl.obtenerTipoCliente(iua))
			.thenReturn(tipoCli);
		
		PowerMockito.when(DocumentarProductoClienteDaoImpl.existeProductoPorIuaYServSiam(iua, tipoCli.getTipoServicioSiam()))
			.thenReturn("idProdDatos");
		
		PowerMockito.when(DocumentarProductoClienteDaoImpl.existeProductoPorIuaYServSiam(iua, ConstantesProvision.VOZ_BANDA_BASE))
			.thenReturn("idProdVoz");
		
		// ACT
        // ------------------ When
        //
		
		//------------------- 3. ACTUAL EXECUTION
		
		//DocumentarProductoCliente.setAccesoBD(mockAccesoBD); //si se instancia dentro del m??todo!!! (ya lo hacemos en m??todo beforeEach)
		
		datosSalida = DocumentarProductoCliente.documentarProductoCliente(datosEntrada);
		
		logger.info("Datos Salida: "+ datosSalida.getResultado());
		logger.info("Codigo salida: "+ datosSalida.getCodResultado());
		logger.info("Desc Salida: "+ datosSalida.getDescripcionResultado());
		
		// ASSERT
        // ------------------ Then
        //
		
		//------------------- 4. VARIFYING CALLS
		
		//SE VERIFICA SI SE LLAM?? O NO A X M??TODO, X NRO DE VECES
		
		//la forma de hacerlo es:
		//1. se declara la intenci??n de verificaci??n
		//2. se hace un fake-call al m??todo a comprobar (no se ejecutar?? pues es un mock, pero hace falta el llamado)
		//nota: si se verifica que se llam?? i.e. 3 veces, s??lo har?? falta usar una instrucci??n de llamado en el paso 2!!!

		PowerMockito.verifyStatic(AccesoBD.class, Mockito.atLeastOnce());
			AccesoBD.cerrarConexionesAbiertas();
		
		PowerMockito.verifyStatic(TrazaWsDao.class, Mockito.atLeastOnce());	
			TrazaWsDao.insertarDocumentarProductoCliente(datosEntrada, datosSalida);
			
		//ESTOS 5 PUDIERAN SER COMUNES A TODOS LOS FLUJOS! (.conectar, .setAccesoBD, .desconectar, .getAccesoBD, .commit)
		//o NO, pq habr??n TEST que se quiera que arroje una exception para comprobar que funciona bien y no pasar?? por el de commit().
		Mockito.verify(mockAccesoBD, Mockito.times(1));
			mockAccesoBD.conectar();
			
		PowerMockito.verifyStatic(DatosThread.class, Mockito.times(1));
			DatosThread.setAccesoBD(mockAccesoBD);
			
		Mockito.verify(mockAccesoBD, Mockito.times(1)).desconectar();
		
		PowerMockito.verifyStatic(DatosThread.class, Mockito.times(2));
			DatosThread.getAccesoBD();
			
		Mockito.verify(mockAccesoBD, Mockito.times(1)).commit();
		
		PowerMockito.verifyStatic(ValidacionOperacion.class, Mockito.times(1));
			ValidacionOperacion.validaParamDocumentarProductoCliente(datosEntrada);
		
		PowerMockito.verifyStatic(SwitchUniparDAO.class, Mockito.times(1));
			SwitchUniparDAO.obtenerTipoOperacion(operacion);
		
		PowerMockito.verifyStatic(SwitchUniparDAO.class, Mockito.times(1));
			SwitchUniparDAO.estaSwitchActivo();
			
				//NO SE DEBEN LLAMAR (en mi proceso)!!!
				
				PowerMockito.verifyStatic(SwitchUniparDAO.class, Mockito.never());
					SwitchUniparDAO.verificarSwitch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
			
				PowerMockito.verifyStatic(SwitchUniparDAO.class, Mockito.never());
					SwitchUniparDAO.verificarSwitchCobre(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
			
				PowerMockito.verifyStatic(SwitchUniparDAO.class, Mockito.never());
					SwitchUniparDAO.verificarSwitchCobreIndirecto(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
			
				PowerMockito.verifyStatic(SwitchUniparDAO.class, Mockito.never());
					SwitchUniparDAO.verificarSwitchNeba(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
		PowerMockito.verifyStatic(SwitchUniparDAO.class, Mockito.times(1));
			SwitchUniparDAO.verificarSwitchGenerico(sistema, telefono, "", datosEntradaInitialToString);
			
		//SWITCH PHYSIS
			
		PowerMockito.verifyStatic(SwitchUniparDAO.class, Mockito.atLeast(1));
			SwitchUniparDAO.registrarSalidaSwitch(Mockito.anyString(), Mockito.anyString());
			
		PowerMockito.verifyStatic(AccesoBD.class, Mockito.atLeastOnce());
			AccesoBD.ejecutarProcedimiento(Mockito.anyString(), Mockito.anyListOf(String.class));
			
		//SWITCH GIROS	
			
		PowerMockito.verifyStatic(DocumentarProductoClienteDaoImpl.class, Mockito.times(1));
			DocumentarProductoClienteDaoImpl.obtenerIUAPorTelefono(telefono);
			
		PowerMockito.verifyStatic(DocumentarProductoClienteDaoImpl.class, Mockito.times(1));
			DocumentarProductoClienteDaoImpl.obtenerTipoCliente(iua);
			
		PowerMockito.verifyStatic(DocumentarProductoClienteDaoImpl.class, Mockito.times(1));
			DocumentarProductoClienteDaoImpl.existeProductoPorIuaYServSiam(iua, datosEntrada._getTipoCliente().getTipoServicioSiam());
			
		PowerMockito.verifyStatic(DocumentarProductoClienteDaoImpl.class, Mockito.times(1));
			DocumentarProductoClienteDaoImpl.existeProductoPorIuaYServSiam(iua, ConstantesProvision.VOZ_BANDA_BASE);
			
		PowerMockito.verifyStatic(DocumentarProductoClienteDaoImpl.class, Mockito.times(1));
			DocumentarProductoClienteDaoImpl.actualizarProducto(Mockito.any(ProductoCliente.class));
			
		//------------------- 5. ASSERTIONS
		
		//PARA LAS ASERCIONES SE NECESITAN DATOS DE SALIDA, EN ESTE CASO DISPONEMOS DE MUY POCOS
		//PERO NOS APOYAMOS VERIFICANDO LLAMADOS A LOS M??TODOS DURANTE LA EJECUCI??N
			
		Assert.assertEquals(
				new DocumentarProductoClienteSalida(ConstantesCobertura.OK, "000", "Operacion realizada correctamente").toString(), 
				datosSalida.toString());
		
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "TEST FAILED - " + new Object(){}.getClass().getEnclosingMethod().getName() + " -->\n" + e.getMessage();
			System.err.println(msg);
			Assert.fail(msg);//SIN ESTO, SE REPORTAR?? QUE LA PRUEBA NO TUVO ERRORES, _TODO OK
		}
	}
	
	
	/**
	 * TEST UNITARIO
	 * Usamos UT_nombreClase_nombreMetodo
	 * */
	@Test
	public void UT_documentarProductoCliente_documentarProductoCliente() {
		try {
		// ARRANGE
        // ------------------ GIVEN
        //
			
		//------------------- 1. PREPARING VARIABLES
		
		Map<String, Integer> availableOpsMap = new TreeMap<String, Integer>();
			
		//------------------- 2. DECLARING & SETTING MOCKS BEHAVIOR
	
		DocumentarProductoClienteEntrada datosEntrada = Mockito.mock(DocumentarProductoClienteEntrada.class);
		DocumentarProductoClienteSalida datosSalida = Mockito.mock(DocumentarProductoClienteSalida.class);
		
		AccesoBD mockAccesoBD = Mockito.mock(AccesoBD.class);
		
		DocumentarProductoClienteImpl mockDocProdCliImpl = Mockito.mock(DocumentarProductoClienteImpl.class);
		
		Mockito.when(mockDocProdCliImpl.documentarProducto(datosEntrada))
		.thenReturn(datosSalida);
		
		//cuando se llame al constructor, con esos objetos exactamente (as?? aseguramos que los tome de la variable global), devolver?? el mock establecido!
		PowerMockito.whenNew(DocumentarProductoClienteImpl.class)
		.withArguments(mockAccesoBD, availableOpsMap)
		//.withAnyArguments()
		//.withNoArguments()
		.thenReturn(mockDocProdCliImpl);
			
		// ACT
        // ------------------ WHEN
        //
		
		//------------------- 3. ACTUAL EXECUTION
		
		DocumentarProductoCliente.setAccesoBD(mockAccesoBD);
		DocumentarProductoCliente.setAvailableOperationsMap(availableOpsMap);
		
		DocumentarProductoClienteSalida salida = DocumentarProductoCliente.documentarProductoCliente(datosEntrada);
			
		// ASSERT
        // ------------------ THEN
        //
		
		//------------------- 4. VARIFYING CALLS	

		//que se llam?? al constructor con tales argumentos. As?? sabemos que los toma de la variable global
		PowerMockito.verifyNew(DocumentarProductoClienteImpl.class).withArguments(mockAccesoBD, availableOpsMap);
		
		Mockito.verify(mockDocProdCliImpl, Mockito.times(1)).documentarProducto(datosEntrada);
		
		PowerMockito.verifyStatic(AccesoBD.class);
		AccesoBD.cerrarConexionesAbiertas();
		
		Mockito.verify(datosEntrada, Mockito.atMost(1)).setOperacion("OPERACION_NO_INFORMADA");	
		
		PowerMockito.verifyStatic(TrazaWsDao.class);
		TrazaWsDao.insertarDocumentarProductoCliente(datosEntrada, datosSalida);
			
		//------------------- 5. ASSERTIONS
		
		Assert.assertEquals(datosSalida, salida);
		
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "TEST FAILED - " + new Object(){}.getClass().getEnclosingMethod().getName() + " -->\n" + e.getMessage();
			System.err.println(msg);
			Assert.fail(msg);//SIN ESTO, SE REPORTAR?? QUE LA PRUEBA NO TUVO ERRORES, _TODO OK
		}
	}
	
	
	//HACER TEST PARA 
	//AccesoBD.ejecutarProcedimiento(String procedimiento, List<String> datosEntrada)
		//pero no debe llamar a BBDD porque afectar?? directamente cada vez que se ejecute el TEST.
			//a menos que sea una BBDD en memoria (para pruebas), pero en este caso no existen PLs, es m??s para hacer consultas, guardar y consultar, etc.
		//debe comprobar que la l??gica est?? bien hecha simplemente.
		
	
	
	
	
}
