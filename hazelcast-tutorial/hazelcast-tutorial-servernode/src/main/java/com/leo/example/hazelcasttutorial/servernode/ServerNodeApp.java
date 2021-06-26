package com.leo.example.hazelcasttutorial.servernode;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;
import java.util.Queue;

/**
 * http://svn.wso2.org/repos/wso2/scratch/hazelcast/hazelcast-2.3.1/docs/manual/multi_html/index.html
 * http://svn.wso2.org/repos/wso2/scratch/hazelcast/hazelcast-2.3.1/docs/manual/multi_html/ch01.html
 * */
public class ServerNodeApp {

	public static void main(String[] args) {
		HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
		
		Map<Integer, String> mapCustomers = hzInstance.getMap("customers");
        mapCustomers.put(1, "Joe");
        mapCustomers.put(2, "Ali");
        mapCustomers.put(3, "Avi");

        System.out.println("Customer with key 1: "+ mapCustomers.get(1));
        System.out.println("Map Size:" + mapCustomers.size());

        Queue<String> queueCustomers = hzInstance.getQueue("customers");
        queueCustomers.offer("Tom");
        queueCustomers.offer("Mary");
        queueCustomers.offer("Jane");
        System.out.println("First customer: " + queueCustomers.poll());
        System.out.println("Second customer: "+ queueCustomers.peek());
        System.out.println("Queue size: " + queueCustomers.size());
	}

}
