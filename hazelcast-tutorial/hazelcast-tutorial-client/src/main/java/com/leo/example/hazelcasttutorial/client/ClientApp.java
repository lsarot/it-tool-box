package com.leo.example.hazelcasttutorial.client;

import java.io.Serializable;
import java.time.Instant;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.map.IMap;
import com.leo.example.hazelcasttutorial.commons.MyRunnable;

/**
 * http://svn.wso2.org/repos/wso2/scratch/hazelcast/hazelcast-2.3.1/docs/manual/multi_html/index.html
 * http://svn.wso2.org/repos/wso2/scratch/hazelcast/hazelcast-2.3.1/docs/manual/multi_html/ch01.html
 * */
public class ClientApp {

	public static void main(String[] args) {
		ClientConfig clientConfig = new ClientConfig();
        //clientConfig.addAddress("127.0.0.1:5701");
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        
        IMap map = client.getMap("customers");
        System.out.println("Map Size:" + map.size());
        
        IExecutorService executorService = client.getExecutorService("HzC-executorService");
        
        long ts = Instant.now().toEpochMilli();
        for (int i = 0; i < 10_000; i++) {
        	final int _i = i;
        	executorService.execute(new MyRunnable(_i, ts));
		}
        
	}

}
