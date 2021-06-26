package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.*;

import extras.Bean;

import javax.annotation.Resource;
import javax.jms.*;  

public class MySender {


	//NOTAS:

	//SI QUEREMOS HACER UN MÉTODO GENÉRICO PARA ENVIAR, YA SEA A QUEUE O TOPIC, AMBOS IMPLEMENTAN TIPO javax.jms.Destination (trabaja igual pero usando el Destination)


	//PARA OBTENER EL CONTEXTO INICIAL (GLASSFISH 4.1.2)
	private static Context getInitialContextGF() throws NamingException {
		//NOTAR QUE NO HIZO FALTA SINO USAR EL INITIALCONTEXT POR DEFECTO SIN PASAR PROPERTIES O HASHTABLE CON PARÁMETROS
		
		//ESTOS SÓLO SI QUIERO USARLO REMOTAMENTE (AUNQUE SEA EL MISMO EQUIPO, SI LO CORRO FUERA DEL IDE YA ES REMOTO!)
		System.setProperty("org.omg.CORBA.ORBInitialHost", "192.168.0.180");
		System.setProperty("org.omg.CORBA.ORBInitialPort", "3700");

		Hashtable<String, String> hashTable = new Hashtable<>();
		hashTable.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.SerialInitContextFactory");
		hashTable.put(Context.STATE_FACTORIES, "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
		hashTable.put(Context.URL_PKG_PREFIXES, "com.sun.enterprise.naming");
		//hashTable.put("org.omg.CORBA.ORBInitialHost", "localhost");
		//hashTable.put("org.omg.CORBA.ORBInitialPort", "3700");
		//hashTable.put(Context.PROVIDER_URL, "iiop://localhost:3700");
		//hashTable.put(Context.SECURITY_PRINCIPAL, "username");
		//hashTable.put(Context.SECURITY_CREDENTIALS, "passw");

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

	//-----------------------------------------------------------------------------------
	//ENVIANDO OBJETO Y NO SÓLO TEXTO
	//USAMOS DESTINATION COMO GENÉRICO

	public static void main(String[] args) {
		try {
			Context ctx = getInitialContextGF();

			Destination d = (Destination) ctx.lookup("jms/myQueue");
			JMSContext jmsCtx = ((ConnectionFactory) ctx.lookup("jms/myQueueConnectionFactory")).createContext();
			JMSProducer jmsProducer = jmsCtx.createProducer();

			//creamos un objeto serializable
			Bean hijo = new Bean( "Hijo", "Toulouse", 10, null );
			Bean miObjetoSerializable = new Bean( "Padre", "Paris", 40, hijo );

			//enviamos el objeto serializable
			jmsProducer.send(d, miObjetoSerializable);

			System.out.println("Objeto enviado al destino!");
		} catch (Exception e) {e.printStackTrace();}
	}

	//-----------------------------------------------------------------------------------
	//USANDO JMSCONTEXT Y TOPIC

	/*public static void main(String[] args) {
		try {
			Context ctx = getInitialContextGF();

			Topic t = (Topic) ctx.lookup("jms/myTopic");
			JMSContext jmsCtx = ((ConnectionFactory) ctx.lookup("jms/myTopicConnectionFactory")).createContext();
			JMSProducer jmsProducer = jmsCtx.createProducer();

			BufferedReader b=new BufferedReader(new InputStreamReader(System.in));  
			while(true) {  
				System.out.println("Enter message, 'end' to terminate:");  String s=b.readLine();  
				if (s.equals("end"))  
					break;  
				jmsProducer.send(t, s);  
			}
		} catch (Exception e) {e.printStackTrace();}
	}*/

	//-----------------------------------------------------------------------------------
	//USANDO JMSCONTEXT Y QUEUE

	/*public static void main(String[] args) {  
		try {
			Context ctx = getInitialContextGF();

			Queue q = (Queue) ctx.lookup("jms/myQueue");
			JMSContext jmsCtx = ((ConnectionFactory) ctx.lookup("jms/__defaultConnectionFactory")).createContext();
			//TAMBIÉN SIRVE USAR EL LOGICAL JNDI NAME: java:comp/DefaultJMSConnectionFactory QUE SÓLO LO TIENE LOS DE TIPO CONN FACTORY NORMAL, NO LOS QUEUE/TOPIC CONN FACTORY
			JMSProducer jmsProducer = jmsCtx.createProducer();

			BufferedReader b=new BufferedReader(new InputStreamReader(System.in));  
			while(true) {  
				System.out.println("Enter message, 'end' to terminate:");  String s=b.readLine();  
				if (s.equals("end"))  
					break;  
				jmsProducer.send(q, s);  
			}
		} catch(Exception e){System.out.println(e);}  
	}*/

	//-----------------------------------------------------------------------------------
	//MÁS COMPLEJO. BÁSICAMENTE DIFIERE EN QUE TOMA CONNECTION FACTORY, CREA CONNECTION Y SESSION

	/*public static void main(String[] args) {  
		try {
			Context ctx = getInitialContextGF();

			//get the Queue object  
			Queue q=(Queue)ctx.lookup("jms/myQueue");//Topic

			//create and start connection   
			QueueConnectionFactory f=(QueueConnectionFactory)ctx.lookup("jms/myQueueConnectionFactory");//TopicConnectionFactory  
			QueueConnection con=f.createQueueConnection();//TopicConnection
			con.start();

			//create queue session  
			QueueSession ses=con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);//TopicSession  

			//create QueueSender object         
			QueueSender sender=ses.createSender(q);//TopicPublisher publisher=ses.createPublisher(t);

			//create TextMessage object  
			TextMessage msg=ses.createTextMessage();  

			//write message  
			BufferedReader b=new BufferedReader(new InputStreamReader(System.in));  
			while(true) {  
				System.out.println("Enter message, 'end' to terminate:");  String s=b.readLine();  
				if (s.equals("end"))  
					break;  
				msg.setText(s);  
				//send message  
				sender.send(msg);//publisher.publish(msg);  
				//ses.commit(); ..si quiero hacer transaction creo basta con esto
			}

			//connection close  
			con.close();  
		}catch(Exception e){System.out.println(e);}  
	}*/

	//-----------------------------------------------------------------------------------
	//SIMILAR AL DE ARRIBA, PERO SEPARA MÁS LA ESTRUCTURA.
	//CONFIGURADO CON CONTEXTO WILDFLY, CAMBIAR AL USO DE GF Y FUNCIONARÁ MEJOR!

	/*private static QueueConnectionFactory qconFactory;
	private static QueueConnection qcon;
	private static QueueSession qsession;
	private static QueueSender qsender;
	private static Queue queue;
	private static TextMessage msg;

	public static void main(String[] args) throws Exception {
		InitialContext ctx = getInitialContextWF();

		queue = (Queue) ctx.lookup("TestQ");//EN WILDFLY NO SIRVE A MENOS QUE USEMOS UN SÓLO NOMBRE PARA EL QUEUE O TOPIC (i.e. exported/jms/myQueue no sirve!)
		qconFactory = (QueueConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory"	);
		//  If you won't pass jms credential here then you will get [javax.jms.JMSSecurityException: HQ119031: Unable to validate user: null]    
		qcon = qconFactory.createQueueConnection("jmsuser", "jmsuser@123");   //SI ESTÁ ACTIVADA LA VALIDACIÓN, SE REQUERIRÁN CREDENCIALES PARA VARIAS ACCIONES
		qcon.start();

		qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);	

		qsender = qsession.createSender(queue);
		msg = qsession.createTextMessage();	

		continua();
	}

	public static void continua() throws IOException, JMSException {
		String _cad="";
		do {
			System.out.println("Escriba mensaje.. 'salir' para terminar aquí... 'quit' para terminar cliente!");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));  _cad = br.readLine();
			if(_cad.equalsIgnoreCase("quit")) { send("quit"); } else { send(_cad); }
		} while(!_cad.equalsIgnoreCase("salir"));
		close();
	}

	public static void close() throws JMSException {
		qsender.close();
		qsession.close();
		qcon.close();
	}

	public static void send(String message) throws JMSException {
		msg.setText(message);
		//msg.setIntProperty("counter", counter);
		qsender.send(msg);
		//System.out.println(qsender.getDeliveryDelay()+" "+qsender.getDeliveryMode()+" "+qsender.getPriority()+" "+qsender.getTimeToLive()+" "+qsender.getDestination()+" "+qsender.getDisableMessageID());
	}*/

	//-----------------------------------------------------------------------------------
}  