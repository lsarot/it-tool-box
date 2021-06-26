package com.example.leo.hazelcast_jet.kafka;

import com.hazelcast.jet.*;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.kafka.KafkaSources;
import com.hazelcast.jet.pipeline.*;
import org.apache.kafka.common.serialization.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import static com.hazelcast.jet.aggregate.AggregateOperations.counting;
import static com.hazelcast.jet.pipeline.WindowDefinition.sliding;

/** This code lets Jet connect to Kafka and show how many events per second were published to the Kafka topic at a given time:
 * You may run this code from your IDE and it will work, but it will create its own Jet instance. To run it on the Jet instance you already started, use the command line like this:
 * mvn package
 * <path_to_jet>/bin/jet submit target/<this_project.jar>
 * */
public class JetJob {
	
    static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    public static void main(String[] args) {
        Pipeline p = Pipeline.create();
        p.readFrom(KafkaSources.kafka(kafkaProps(), "tweets"))
         .withNativeTimestamps(0)
         .window(sliding(1_000, 500))
         .aggregate(counting())
         .writeTo(Sinks.logger(wr ->
         			String.format("At %s Kafka got %,d tweets per second",
         			TIME_FORMATTER.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(wr.end()), ZoneId.systemDefault())),
         			wr.result())));

        JobConfig cfg = new JobConfig().setName("kafka-traffic-monitor");
        Jet.bootstrappedInstance().newJob(p, cfg);
    }

    private static Properties kafkaProps() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("key.deserializer", LongDeserializer.class.getCanonicalName());
        props.setProperty("value.deserializer", StringDeserializer.class.getCanonicalName());
        props.setProperty("auto.offset.reset", "earliest");
        return props;
    }
}
