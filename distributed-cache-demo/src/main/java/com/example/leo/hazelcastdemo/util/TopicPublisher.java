package com.example.leo.hazelcastdemo.util;

import com.example.leo.redisdemo.util.CustomMessage;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;

/** JUST TO TRY PUBLISHING MESSAGES ON A TOPIC
 * */
public class TopicPublisher {

	public static void main(String[] args) {
		ClientConfig config = new ClientConfig();
		HazelcastInstance hzClient = HazelcastClient.newHazelcastClient(config);
		
		ITopic<CustomMessage> publishTopic = hzClient.getReliableTopic("topic");
		for (int i = 0; i < 3; i++) {
			publishTopic.publish(new CustomMessage("This is a message"));
			try {Thread.sleep(3000);} catch (Exception e) {}
		}
		
	}

}
