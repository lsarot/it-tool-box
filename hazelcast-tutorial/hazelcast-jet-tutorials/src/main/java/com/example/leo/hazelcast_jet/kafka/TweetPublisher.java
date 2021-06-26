package com.example.leo.hazelcast_jet.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;

import java.util.Properties;

/** This code publishes "tweets" (just some simple strings) to a Kafka topic tweets, with varying intensity:
 * 
 * arrancamos esto en el IDE y nos vamos a la clase JetJob
 * */
public class TweetPublisher {

	public static void main(String[] args) throws Exception {
        String topicName = "tweets";
        try (KafkaProducer<Long, String> producer = new KafkaProducer<>(kafkaProps())) {
            for (long eventCount = 0; ; eventCount++) {
                String tweet = String.format("tweet-%0,4d", eventCount);
                producer.send(new ProducerRecord<>(topicName, eventCount, tweet));
                System.out.format("Published '%s' to Kafka topic '%s'%n", tweet, topicName);
                Thread.sleep(20 * (eventCount % 20));
            }
        }
    }

    private static Properties kafkaProps() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("key.serializer", LongSerializer.class.getCanonicalName());
        props.setProperty("value.serializer", StringSerializer.class.getCanonicalName());
        return props;
    }
}
