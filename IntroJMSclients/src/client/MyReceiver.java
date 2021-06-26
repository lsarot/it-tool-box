package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.jms.*;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import extras.Bean;  

public class MyReceiver implements MessageListener {


	//NOTAS:
	
	//SÓLO ME SIRVIÓ DESDE LOS IDES. EJECUTÁNDOLO EN UN SHELL NO FUNCIONÓ!

	//CADA BLOQUE ES UN EJEMPLO.. DESCOMENTARLO PARA PROBARLO.. ARRIBA ALGUNOS MÉTODOS COMUNES.. EN LA CLASE MySender HAY UN EJEMPLO ANÁLOGO PARA CADA CASO PERO SIENDO EL SENDER.

	//PARECE QUE LO MÁS FÁCIL Y NUEVO ES USAR JMSCONTEXT, LOS ÚLTIMOS MÉTODOS SON MÁS COMPLEJOS YA QUE USAN CONNECTION Y SESSION
	//QUIZÁS SI EL SERVIDOR SOLICITA VALIDACIÓN DE CREDENCIALES DE USUARIO SI SE DEBA USAR ESE CAMINO.

	//UN QUEUE O TOPIC CONNECTIONFACTORY EXTIENDEN DE CONNECTIONFACTORY,
	//PUEDO USAR I.E. jms/__defaultConnectionFactory o jms/myQueueConnectionFactory Y AMBAS FUNCIONARÁN AUNQUE EL PRIMERO SEA TIPO GENERAL..

	//USAR DEPENDENCY INJECTION PARA EL CONN FACTORY Y EL QUEUE/TOPIC FUNCIONA CUANDO EL PROYECTO SE DESPLIEGA EN EL SERVIDOR
	//I.E. UN WEB PROJECT CON SERVLETS/JSP O UN JEE APP CON EJBS.

	/* SOBRE EL USO CON CONTEXT DE WILDFLY EN LUGAR DE GLASSFISH:
	 * 		FUNCIONA LA CONEXIÓN, PERO EL CLIENTE SÓLO RECIBE 1 VEZ Y 1 MENSAJE, NO EL LOTE ENVIADO, Y MENOS VARIAS VECES, A VECES NO LO RECIBE A LA PRIMERA,
	 * 		ENTONCES ALGO ESTÁ MAL PQ NO SE SABE CUANDO SE LLAMARÁ A onMessage
	 * */

	//-----------------------------------------------------------------------------------

	//PARA OBTENER EL CONTEXTO INICIAL (GLASSFISH 4.1.2)
	private static Context getInitialContextGF() throws NamingException {
		//NOTAR QUE NO HIZO FALTA SINO USAR EL INITIALCONTEXT POR DEFECTO SIN PASAR PROPERTIES O HASHTABLE CON PARÁMETROS
		
		//ESTOS SÓLO SI QUIERO USARLO REMOTAMENTE
		System.setProperty("org.omg.CORBA.ORBInitialHost", "192.168.0.180");
		System.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
		
		//TRATANDO DE QUE FUNCIONE DESDE UN SHELL CMD EJECUTANDO EL .JAR, PERO NO SIRVE SINO DENTRO DE LOS IDES
			//System.setProperty("addresslist", "192.168.0.180:7676");
			//System.setProperty("addresslist", "mq://192.168.0.180:7676/jms");
			//System.setProperty("imqAddressList", "192.168.0.180:7676");
			//System.setProperty("imqAddressList", "mq://192.168.0.180:7676/jms");
		
		Hashtable<String, String> hashTable = new Hashtable<>();
		hashTable.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.SerialInitContextFactory");
		hashTable.put(Context.STATE_FACTORIES, "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
		hashTable.put(Context.URL_PKG_PREFIXES, "com.sun.enterprise.naming");
		hashTable.put("org.omg.CORBA.ORBInitialHost", "192.168.0.180");
		hashTable.put("org.omg.CORBA.ORBInitialPort", "3700");
		hashTable.put(Context.PROVIDER_URL, "iiop://192.168.0.180:3700");
		//hashTable.put(Context.SECURITY_PRINCIPAL, "admin");
		//hashTable.put(Context.SECURITY_CREDENTIALS, "admin");
		
		Context ctx = new InitialContext();
		return ctx;
	}

	//PARA OBTENER EL CONTEXT INICIAL (WILDFLY 11)
	private static InitialContext getInitialContextWF() throws NamingException {
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
		props.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");   // NOTICE: "http-remoting" and port "8080"
		props.put(Context.SECURITY_PRINCIPAL, "jmsuser");
		props.put(Context.SECURITY_CREDENTIALS, "jmsuser@123");
		//props.put("jboss.naming.client.ejb.context", true);
		InitialContext context = new InitialContext(props); 
		System.out.println("\n\tGot initial Context: "+context);
		return context;
	}

	//PARA CONFIRMAR SI CONTINÚA O TERMINA LA EJECUCIÓN DE LA APP
	private static void continuar() throws IOException {
		System.out.println("Receiver is ready, waiting for messages...");
		String _cad="";
		do {
			System.out.println("Write end to terminate!");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));   _cad = br.readLine();
			if(_cad.equalsIgnoreCase("end")) { break; }
		} while(true);
		/*CERRAR LOS OTROS RECURSOS : context, queue, session, etc. */
	}

	//EN LOS CASOS QUE SE TRABAJE CON ESTE MÉTODO (LLAMADO POR EL CONTENEDOR)
	public void onMessage(Message m) {  
		try{  
			TextMessage msg=(TextMessage) m;  
			System.out.println("-->following message is received: "+msg.getText());  
		}catch(JMSException e){System.out.println(e);}  
	}

	//-----------------------------------------------------------------------------------
	//RECIBE OBJETO SERIALIZABLE.. TRABAJA CON JMSCONTEXT

	public static void main(String[] args) {
		try {
			Context ctx = getInitialContextGF();

			Queue q = (Queue) ctx.lookup("jms/myQueue");
			JMSContext jmsCtx = ((ConnectionFactory) ctx.lookup("jms/myQueueConnectionFactory")).createContext();
			JMSConsumer jmsConsumer = jmsCtx.createConsumer(q);

			System.out.println("Waiting for object to be available on queue...");
			Bean bean = jmsConsumer.receiveBody(Bean.class);
			System.out.println(bean);

			System.out.println("Cliente finalizó!.");
		} catch (Exception e) {e.printStackTrace();}
	}

	//-----------------------------------------------------------------------------------
	//USA JMSCONTEXT. Trabaja con TOPIC
	//cuando se usa topic (modelo pub/subs), los suscriptores pueden ser durable y non-durable.. esto es si reciben o no los msjs enviados en su ausencia!

	/*public static void main(String[] args) {
		try {
			Context ctx = getInitialContextGF();

			MyReceiver receiver = new MyReceiver();
			Topic t = (Topic) ctx.lookup("jms/myTopic");
			JMSContext jmsCtx = ((ConnectionFactory) ctx.lookup("jms/myTopicConnectionFactory")).createContext();
			jmsCtx.createConsumer(t).setMessageListener(receiver);

			continuar();
		} catch (Exception e) {e.printStackTrace();}
	}*/

	//-----------------------------------------------------------------------------------
	//USA JMSCONTEXT. No setea listener por lo que no se llamará a onMessage

	/*public static void main(String[] args) {
		try {
			Context ctx = getInitialContextGF();

			Queue q = (Queue) ctx.lookup("jms/myQueue");
			JMSContext jmsCtx = ((ConnectionFactory) ctx.lookup("jms/__defaultConnectionFactory")).createContext();
			JMSConsumer jmsConsumer = jmsCtx.createConsumer(q);

			//.receiveBody funciona sin llamar a onMessage, BLOQUEA MIENTRAS
			//.receiveBodyNoWait PARA NO BLOQUEAR
				//.receive y .receiveNoWait recuperan el Message completo, incluye Head, Properties y Body 
			String mensaje = jmsConsumer.receiveBody(String.class);
			System.out.println("Mensaje recibido: " + mensaje);

			System.out.println("Cliente finalizó!.");
		} catch (Exception e) {e.printStackTrace();}
	}*/

	//-----------------------------------------------------------------------------------
	//MÁS SIMPLE. USA JMSCONTEXT

	/*public static void main(String[] args) {
		try {
			Context ctx = getInitialContextGF();

			MyReceiver receiver = new MyReceiver();
			Queue q = (Queue) ctx.lookup("jms/myQueue");
			JMSContext jmsCtx = ((ConnectionFactory) ctx.lookup("jms/__defaultConnectionFactory")).createContext();//java:comp/DefaultJMSConnectionFactory
			jmsCtx.createConsumer(q).setMessageListener(receiver);

			continuar();
		} catch(Exception e){System.out.println(e);}
	}*/

	//-----------------------------------------------------------------------------------
	//MÁS COMPLEJO. BÁSICAMENTE DIFIERE EN QUE TOMA CONNECTION FACTORY, CREA CONNECTION Y SESSION

	/*public static void main(String[] args) {  
		try {
			Context ctx = getInitialContextGF();

			MyReceiver my_receiver = new MyReceiver();

			//get the Queue object
			Queue q=(Queue)ctx.lookup("jms/myQueue");// o Topic

			//get the connection factory   	
			QueueConnectionFactory f=(QueueConnectionFactory)ctx.lookup("jms/myQueueConnectionFactory");  

			//create and start connection
			QueueConnection con=f.createQueueConnection();  
			con.start();

			//create Queue session  
			QueueSession ses=con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);//TopicSession		

			//create QueueReceiver  
			QueueReceiver receiver=ses.createReceiver(q);//TopicSubscriber subscriber=ses.createSubscriber(t);

			//register the listener object with receiver  
			receiver.setMessageListener(my_receiver);
			
			//ses.commit(); ..si quiero usar TRANSACTION sólo uso esto al parecer (se inicia en algún paso anterior pero no usa begin)
			
			continuar();
		} catch(Exception e){System.out.println(e);}  
	}*/

	//-----------------------------------------------------------------------------------
	//SIMILAR AL DE ARRIBA, PERO USA UN BLOQUE SYNCHRONIZED EN LA ESPERA POR MSJS Y LO DESPIERTA AL LLEGAR MSJ 'quit'.
	//CREÓ UN MÉTODO close. onMessage LO PONEMOS AQUÍ ABAJO PARA FACILITAR LA LECTURA.
	//SE SEPARA MEJOR LA ESTRUCTURA, sacando variables fuera de métodos, un método close, revisando tipo de msj recibido y usando un synchronized.

	//CONFIGURADO CON CONTEXTO WILDFLY, CAMBIAR AL USO DE GF Y FUNCIONARÁ MEJOR!

	/*private static QueueConnectionFactory qconFactory;
	private static QueueConnection qcon;
	private static QueueSession qsession;
	private static QueueReceiver qReceiver;
	private static Queue queue;
	private static TextMessage msg;
	private static boolean _quit = false;

	public static void main(String[] args) throws Exception {
		InitialContext ctx = getInitialContextWF();
		MyReceiver my_receiver = new MyReceiver();

		queue = (Queue) ctx.lookup("TestQ");//EN WILDFLY NO SIRVE A MENOS QUE USEMOS UN SÓLO NOMBRE PARA EL QUEUE O TOPIC (i.e. exported/jms/myQueue no sirve!)
		qconFactory = (QueueConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory"	);
		//  If you won't pass jms credential here then you will get [javax.jms.JMSSecurityException: HQ119031: Unable to validate user: null]    
		qcon = qconFactory.createQueueConnection("jmsuser", "jmsuser@123");   //SI ESTÁ ACTIVADA LA VALIDACIÓN, SE REQUERIRÁN CREDENCIALES PARA VARIAS ACCIONES
		qcon.start();

		qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);	

		qReceiver = qsession.createReceiver(queue);
		qReceiver.setMessageListener(my_receiver);	

		my_receiver.continua();
	}

	private void continua() throws JMSException {
		System.out.println("JMS Ready To Receive Messages (To quit, send a \"quit\" message from WildFlyJmsQueueSender.class).");
		// Waiting until a "quit" message has been received.
		synchronized(this) {
			while (! MyReceiver._quit) {//un boolean declarado global
				try {
					this.wait();
					//este wait se hace sobre la instancia de my_receiver.. dicha instancia se pasa a session.createReceiver, 
					//entonces onMessage se llamará sobre esa instancia, y al hacer notifyAll despierta esto!
					System.out.println("Thread awaked!");
				} catch (InterruptedException ie) { ie.printStackTrace(); }
			}
		}
		close();
	}

	public static void close() throws JMSException {
		qReceiver.close();
		qsession.close();
		qcon.close();
	}

	public void onMessage(Message msg) {
		try {
			String msgText;
			if (msg instanceof TextMessage) {
				msgText = ((TextMessage)msg).getText();
			} else {
				msgText = msg.toString();
			}
			System.out.println("\n<Msg_Receiver> "+ msgText );
			if (msgText.equalsIgnoreCase("quit")) {
				synchronized(this) {
					MyReceiver._quit = true;
					this.notifyAll(); // Notify main thread to quit... se libera el bloqueo hecho en main sobre instancia my_receiver
				}
			}
		} catch (JMSException jmse) { jmse.printStackTrace(); }
	}*/

	//-----------------------------------------------------------------------------------

}  
