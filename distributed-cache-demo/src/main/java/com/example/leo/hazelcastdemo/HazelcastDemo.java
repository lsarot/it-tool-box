package com.example.leo.hazelcastdemo;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.example.leo.redisdemo.util.CustomMessage;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;

/** HAZELCAST in-memory data grid.
 * https://www.baeldung.com/java-hazelcast
 * https://docs.hazelcast.org/docs/latest/manual/html-single/index.html
 * https://jet-start.sh/
 * 
 * MANUAL:
 * 		file:///Users/Leo/Downloads/hazelcast-management-center-4.0.3/docs/html/index.html
 * 
 * It comes in different editions (open-source, enterprise and enterprise HD)
 * It offers various features such as Distributed Data Structure, Distributed Compute, Distributed Query, etc
 * 
 * The architecture supports high scalability and data distribution in a clustered environment.
 * It supports auto-discovery of nodes and intelligent synchronization.
 * 		Members (also called nodes) automatically join together to form a cluster. This automatic joining takes place with various discovery mechanisms that the members use to find each other. (multicast, TCP/IP)
 * 
 * Why Hazelcast ?
 * 		https://docs.hazelcast.org/docs/3.7/manual/html-single/index.html#why-hazelcast
 * */
/* SPRING BOOT ?
 * com.hazelcast : hazelcast , com.hazelcast : hazelcast-spring
 * then in a @Configuration class:
    @Bean public Config hazelCastConfig() {
        Config config = new Config();
        config
        .setInstanceName("hazelcast-instance")
        .addMapConfig(
            new MapConfig()
                    .setName("configuration")
                    .setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                    .setEvictionPolicy(EvictionPolicy.LRU)
					.setTimeToLiveSeconds(-1)
		);
        return config;
    }
 * and then inject:
	@Autowired HazelcastInstance hazelcastInstance;
 * */
public class HazelcastDemo {

	//Hazelcast client allows us to do all Hazelcast operations without being a member of the cluster. It connects to one of the cluster members and delegates all cluster-wide operations to it.
	private static HazelcastInstance hzClient;
	
	public static void main(String[] args) {
		new HazelcastDemo().run();
	}

	private void run() {
		configuration();
		collections();
		topic();
		services();
		
		dummiesPerformanceTest();
	}

	
	/**
	 * While Hazelcast is starting up, it looks for hazelcast.config system property.
	 * If it is set, its value is used as the path. If the above system property is not set, Hazelcast then checks whether there is a hazelcast.xml file in the working directory.
	 * If not, then it checks whether hazelcast.xml exists on the classpath. If none of the above works, Hazelcast loads the default configuration, i.e. hazelcast-default.xml that comes with hazelcast.jar.
	 * */
	/* hazelcast.xml example:
	<?xml version="1.0" encoding="UTF-8"?>
	<hazelcast xsi:schemaLocation="http://www.hazelcast.com/schema/config hazelcast-config-3.7.xsd"  xmlns="http://www.hazelcast.com/schema/config"  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	    <network>
	        <port auto-increment="true" port-count="20">5701</port>
	        <join>
	            <multicast enabled="false">
	        	</multicast>
		        <tcp-ip enabled="true">
		            <member>machine1</member>
		        	<member>localhost</member>
		        </tcp-ip>
	        </join>
	    </network>
	</hazelcast>
	 * 
	 * programmatically, on this method.
	 * */
	private void configuration() {
		ClientConfig config = new ClientConfig();
		//GroupConfig groupConfig = config.getGroupConfig();
		//groupConfig.setName("dev");
		//groupConfig.setPassword("dev-pass");
				//The default username and password to access the cluster are dev and dev-pass.
		//config.getNetworkConfig();
		//config.getSecurityConfig().setUsernamePasswordIdentityConfig("dev", "dev-pass");
		hzClient = HazelcastClient.newHazelcastClient(config);
		
		
		// By default, Hazelcast will try 100 ports to bind (from the one given).
		// If we want to choose to use only one port, we can disable the auto-increment feature of a port by setting auto-increment to false.
		/*
		Config config0 = new Config();
		NetworkConfig network = config0.getNetworkConfig();
		network.setPort(5701).setPortCount(20);
		network.setPortAutoIncrement(true);
		JoinConfig join = network.getJoin();
		join.getMulticastConfig().setEnabled(false);
		join.getTcpIpConfig()
			.addMember("192.168.0.180")
			.addMember("machine1")
			.addMember("localhost").setEnabled(true);
		*/
		
		
		// NO HACE QUE SE MUESTRE ESTADÍSTICAS EN MAN CENTER (sobre el Topic queríamos ver), tiene que ser sobre un nodo!
		ManagementCenterConfig manCenterCfg = new ManagementCenterConfig();
		manCenterCfg
				.setScriptingEnabled(true)
				.addTrustedInterface("http://localhost:8083/mancenter");
	}

	
	private void collections() {
		
		// :::: DISTRIBUTED MAP
		
		// Internally, Hazelcast will partition the map entries and distribute and replicate the entries among the cluster members. 
		IMap<Long, String> map = hzClient.getMap("data");
		// put
		FlakeIdGenerator idGenerator = hzClient.getFlakeIdGenerator("newid");
		for (int i = 0; i < 10; i++) {
		    map.put(idGenerator.newId(), "message" + 1);
		}
		// get
		map.forEach((k,v)->System.out.println(k + " -> " + v));
		
	}
	
	
	private void topic() {
		
		// :::: TOPIC
		
		ITopic<CustomMessage> subscribeTopic = hzClient.getReliableTopic("topic");
		subscribeTopic.addMessageListener(msg -> {
			System.out.println(msg.getMessageObject().getMessage() + "   @" + msg.getPublishTime());
		});
		
		ITopic<CustomMessage> publishTopic = hzClient.getReliableTopic("topic");
		for (int i = 0; i < 3; i++) {
			publishTopic.publish(new CustomMessage("This is a message"));
			try {Thread.sleep(3000);} catch (Exception e) {}
		}
		
	}
	
	
	private void services() {
		
		// :::: EXECUTOR SERVICE
		
		IExecutorService execS = hzClient.getExecutorService("myExecutorService");
		for (int i = 0; i < 18; i++) {
			execS.submit(new MyRunnable());
		}		
	}
	
	
	public static class MyRunnable implements Runnable, Serializable {
		private static final long serialVersionUID = 3282230746298351085L;
		@Override	public void run() {
			try {Thread.sleep(3000);} catch (Exception e) {}
		}
	}
	
	
	/**
	 * insertamos secuencial, no es lo habitual
	 * recuperamos random, es lo habitual
	 * 
	 * con un ThreadPool, 3 nodos en el clúster, igual demora mucho la inserción y lectura.
	 * se intentó también con un executorService del mismo cluster hazelcast, demoró 250seg, nunca 15 como las librerías locales
	 * al menos el mapa no tuvo problema para guardar los 5MM
	 * */
	private void dummiesPerformanceTest() {
		System.out.println("Dummies Performance Test");
		long init = System.currentTimeMillis();
		IMap<String, String> map0 = hzClient.getMap("dummieMap");
		map0.destroy(); // lo eliminamos previamente
		IMap<String, String> map = hzClient.getMap("dummieMap");
		
		//ExecutorService exS = java.util.concurrent.Executors.newFixedThreadPool(8);
		IExecutorService exS_hz = hzClient.getExecutorService("ex-service");
		
		for (int i = 0; i < 5_000; i++) {
			final int k = i;
			//exS.submit(() -> map.put("key-"+k, "abcdefghijklmnopqrstuvwxyz"));
			
			exS_hz.execute(new MyRunnableDummieTest(k, true, map));
			
			if (i % 200000 == 0) {System.out.println("->"+i);}
		}
		
		try {
			boolean b = exS_hz.awaitTermination(5, TimeUnit.MINUTES);
			System.out.println("writes done: " + b);
		} catch (InterruptedException e) {}

		for (int i = 0; i < 5_000; i++) {
			//map.get("key-"+ThreadLocalRandom.current().nextInt(0, 5000000 + 1));
			
			exS_hz.execute(new MyRunnableDummieTest(0, false, map));
			
			if (i % 200000 == 0) {System.out.println("->"+i);}
		}
		
		try {
			boolean b = exS_hz.awaitTermination(5, TimeUnit.MINUTES);
			System.out.println("reads done: " + b);
		} catch (InterruptedException e) {}
		
		System.out.println("Dummy test took ms : " + (System.currentTimeMillis()-init));
	}
	
	
	public static class MyRunnableDummieTest implements Runnable, Serializable {
		
		private static final long serialVersionUID = 1L;
		private int k;
		private boolean isInsert;
		private IMap<String, String>  map;
		
		public MyRunnableDummieTest(int k, boolean isInsert, IMap<String, String>  map) {
			this.k = k;
			this.isInsert = isInsert;
			this.map = map;
		}
		
		@Override	public void run() {
			//IMap<String, String> map = hzClient.getMap("dummieMap");
			if (this.isInsert)
				map.put("key-"+k, "abcdefghijklmnopqrstuvwxyz");
			else
				map.get("key-"+ThreadLocalRandom.current().nextInt(0, 5000000 + 1));
		}
	}
	
}
