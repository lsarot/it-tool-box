package com.example.leo.hazelcastdemo.util;

import com.example.leo.redisdemo.util.CustomMessage;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;

/**
 * When we start the ServerNode application, we can see the following text in the console which means that we created a new Hazelcast node in our JVM which will have to join the cluster.
 * To create multiple nodes we can start multiple instances of ServerNode application. Hazelcast will automatically create and add a new member to the cluster.
 * 
 * Members [2] {
	  Member [192.168.1.105]:5701 - 899898be-b8aa-49aa-8d28-40917ccba56c
	  Member [192.168.1.105]:5702 - d6b81800-2c78-4055-8a5f-7f5b65d49f30 this
	}
 * */
public class ServerNode {

	public static void main(String[] args) {
		// WE JUST RUN ONE OF THIS FOR EACH NODE TO JOIN THE CLUSTER
		HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
		//Hazelcast.newHazelcastInstance(config)
		// now we can use the instance here or retrieve it from another app with the client api. (see HazelcastDemo.java)

		// :::: Management Center Configuration
		// necesita 1GB ram
		
		/*
		 Management center allows us to monitor overall state of clusters, we can also analyze and browse your data structures in detail, update map configurations and take thread dump from nodes.
		 In order to user Hazelcast management center, we can either deploy the mancenter-version.war application into our Java application server/container or we can start Hazelcast Management Center from the command line. We can download the latest Hazelcast ZIP from hazelcast.org. The ZIP contains the mancenter-version.war file.
		 We can configure our Hazelcast nodes by adding the URL of the web application to hazelcast.xml and then have the Hazelcast members communicate with the management center.
		 * Let's configure the management center using declarative configuration:
				<management-center enabled="true">
				    http://localhost:8080/mancenter
				</management-center>
		 and programmatic configuration:
		 * */
		ManagementCenterConfig manCenterCfg = new ManagementCenterConfig();
		manCenterCfg
				.setScriptingEnabled(true)
				.addTrustedInterface("http://localhost:8083/mancenter");
		
		
		// first run Management Center by one of these:
		
		// java -Dhazelcast.mc.http.port=8083 -Dhazelcast.mc.contextPath='hazelcast-mc' -jar hazelcast-management-center-4.0.3.war
				// http://localhost:8083/hazelcast-mc
		
		// Deploying to Application Server (Tomcat, Jetty, etc)
		
		// Using Scripts in the Package
		
		// admin : adm1357*
		
		
		// just to show Topic stats in ManCenter
		ITopic<CustomMessage> publishTopic = hzInstance.getReliableTopic("topic");
		for (int i = 0; i < 3; i++) {
			publishTopic.publish(new CustomMessage("This is a message"));
			try {Thread.sleep(3000);} catch (Exception e) {}
		}
		
	}

}
