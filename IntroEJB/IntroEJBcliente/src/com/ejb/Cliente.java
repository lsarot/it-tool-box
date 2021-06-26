package com.ejb;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ejb.slsb.CalcRemote;

public class Cliente {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext context=null;
		try {
			Properties props = new Properties();
			props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
			props.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");   // NOTICE: "http-remoting" and port "8080"
			props.put(Context.SECURITY_PRINCIPAL, "jmsuser");
			props.put(Context.SECURITY_CREDENTIALS, "jmsuser@123");
			//props.put("jboss.naming.client.ejb.context", true);
			context = new InitialContext(props); 
			System.out.println("\n\tGot initial Context: "+context);     
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//-------------------------------- CLIENTE DEL SESSION BEAN
        CalcRemote proxy = (CalcRemote) context.lookup("IntroEJB/Calc!com.ejb.slsb.CalcRemote");
        		//	java:jboss/exported/IntroEJB/Calc!com.ejb.slsb.CalcRemote ESTE ES EL QUE TIENE CONTEXTO REMOTO, REVISAR EL JNDI TREE EN EL WEB CONTROL PANEL QUE HAY UNOS QUE APARECEN COMO LOCALES
        System.out.println("RESPUESTA DEL SERVIDOR: "+proxy.addition(10, 5));
	    
		proxy.asincronoSinRspta();
        
        
        
		//-------------------------------- CLIENTE JMS, ENVIAMOS UN MSJ AL TOPIC QUE SERÁ CONSUMIDO POR EL MDB
		ConnectionFactory factoria = (ConnectionFactory)context.lookup("jms/RemoteConnectionFactory"); 
		Topic topic = (Topic)context.lookup("TestT");

		Connection conexion = factoria.createConnection("jmsuser", "jmsuser@123");
		Session sesion = conexion.createSession(false, Session.AUTO_ACKNOWLEDGE); 
		MessageProducer productor = sesion.createProducer(topic); 

		conexion.start(); 
		TextMessage mensajeDeTexto = sesion.createTextMessage("Mensaje enviado desde cliente Java a TestT."); 
		productor.send(mensajeDeTexto);
		System.out.println("Mensaje enviado a TestT, revisarlo!");
		conexion.close();
		//--------------------------------
		

		//CONFIGURATION ADD LISTENER PORTS

	}

}
