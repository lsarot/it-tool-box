package com.ejb.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Message-Driven Bean implementation class for: PrimerMBD
 */
@MessageDriven(
		activationConfig = {
				@ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
				@ActivationConfigProperty(
				propertyName="destination", propertyValue="TestT")
		})
public class PrimerMBD implements MessageListener {

    /**
     * Default constructor. 
     */
    public PrimerMBD() {
        // TODO Auto-generated constructor stub
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {//MISMO PROBLEMA QUE APP JMS NORMAL, ONMESSAGE NO ES INVOCADO
    		if(message instanceof TextMessage) {
            String msj="";
			try {
				msj = ((TextMessage)message).getText();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            System.out.println("Mensaje recibido: "+ msj );                              
        }
    }

}
