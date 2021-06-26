package com.example.leo.hazelcastdemo.hazelcast_jet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hazelcast.jet.Traversers.traverseArray;
import static com.hazelcast.jet.aggregate.AggregateOperations.counting;
import static com.hazelcast.function.Functions.wholeItem;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.test.TestSources;


/** What is Hazelcast Jet?

Hazelcast Jet is a distributed data processing engine that treats data as streams. It can process data that is stored in a database or files as well as the data that is streamed by a Kafka server.
It can perform aggregate functions over infinite data streams by dividing the streams into subsets and applying aggregation over each subset. This concept is known as windowing in the Jet terminology.
We can deploy Jet in a cluster of machines and then submit our data processing jobs to it. Jet will make all the members of the cluster automatically process the data. Each member of the cluster consumes a part of the data, and that makes it easy to scale up to any level of throughput.
Here are the typical use cases for Hazelcast Jet:
	    Real-Time Stream Processing
	    Fast Batch Processing
	    Processing Java 8 Streams in a distributed way
	    Data processing in Microservices
	    
	    
https://jet-start.sh/
https://jet-start.sh/docs/get-started/intro

Ver más en proyecto hazelcast-jet-tutorials

 * */
/* we'll create a sample application that takes an input of sentences and a word to find in those sentences and returns the count of the specified word in those sentences. */
public class JetDemo {

	public static void main(String[] args) {
		new JetDemo().run();
	}

	private void run() {
		try {
			firstJob(); // https://jet-start.sh/docs/get-started/first-job
			//wordCounter();
			
		} finally {
			// Lastly, we shut down the Jet instance. It is important to shut it down after our execution has ended, as Jet instance starts its own threads. Otherwise, our Java process will still be alive even after our method has exited.
			Jet.shutdownAll();
		}
	}
	
	// ------------------------------------------
	
	private void firstJob() {
		// EMBEDDED NODE (in this JVM)
		// It will start a full-featured Jet node right there in the JVM where you call it and submit your pipeline to it.
		// If you were submitting the code to an external Jet cluster, the syntax would be the same because JetInstance can represent both an embedded instance or a remote one via a local proxy object. You'd just call a different method to create the client instance.
		// Once you submit a job, it has a life of its own. It is not coupled to the client that submitted it, so the client can disconnect without affecting the job. In our simple code we call job.join() so we keep the JVM alive while the job lasts.
		//JetInstance jet = Jet.newJetInstance();
		
		// EXTERNAL CLUSTER
		/* If you run the application again, it will have the same behavior as before and create an embedded Jet instance.
		 However, if you package your code in a JAR and pass it to jet submit, it will instead return a client proxy that talks to the cluster.
		 We have to configure mainClass on maven or graddle or use bin/jet submit -c <main_class_name> <path_to_JAR_file>
		 Standalone:   bin/jet submit <path_to_JAR_file>
		 Docker:   docker run -it -v <path_to_JAR_file>:/jars hazelcast/hazelcast-jet jet -t 172.17.0.2 submit /jars/<name_of_the_JAR_file>
		 *** As we noted earlier, whether or not you kill the client application, the job keeps running on the server. A job with a streaming source will run indefinitely until explicitly cancelled (jet cancel <job-id>) or the cluster is shut down.
		*/
		JetInstance jet = Jet.bootstrappedInstance();
		// The job is now running on both nodes, but the log output is still appearing on just one of them. This is because the test data source is non-distributed, and we don't have any steps in the pipeline which require data rebalancing.
		// Another thing you may notice here is that the sequence numbers were reset to zero, this is because the test source we're using is not fault-tolerant.

		
		jet.newJob(pipeline_ReadFromGeneratedData_WriteToConsole())
				.join();
	}


	private static final String LIST_NAME = "textList";
	private static final String MAP_NAME = "countMap";
	
	private void wordCounter() {
		List<String> sentences = new ArrayList<>();
	    sentences.add("The first second was alright, but the second second was tough.");
	    sentences.add("Second sentence will include two more instances of second word.");
	    String word = "second";
	    long count = countWord(sentences, word);
	    System.out.printf("%n'%s' count is -> %d%n%n", word, count);
	    assert(count == 3);
	}
    
	
	// ------------------------------------------
	
	
	/** procesó sin problema un flujo de 1MM reg/sec en ficheros de 1MB c/u
	 * De la página:
	 * 		You can instantly react to real-time events with Jet, enriching and applying inference at scale. A single node is capable of windowing and aggregating 100Hz sensor data from 100,000 devices with latencies below 10 milliseconds: that's 10 million events/second.
	 * */
    private Pipeline pipeline_ReadFromGeneratedData_WriteToConsole() {
    	Pipeline p = Pipeline.create();
    	p.readFrom(TestSources.itemStream(10)) // emits SimpleEvents that have an increasing sequence number
	    	   .withoutTimestamps()
	    	   .filter(event -> event.sequence() % 2 == 0) // will discard every other event and keep those with an even (nro par) sequence number.
	    	   //.filter(event -> event.sequence() % 100 == 0)
	    	   .setName("filter out odd numbers")
	    	   //.writeTo(Sinks.filesBuilder("/Users/Leo/Desktop/jetWriteSink").rollByFileSize(1048576).build());
	    	   .writeTo(Sinks.logger());
    	return p;
    }
    
    
    /** A Pipeline forms the basic construct for a Jet application. Processing within a pipeline follows these steps:
		    draw data from a source
		    transform the data
		    drain the data into a sink
		For our application, the pipeline will draw from a distributed List, apply the transformation of grouping and aggregation and finally drain to a distributed Map.
		
		Once we've drawn from the source, we traverse the data and split it around the space using a regular expression. After that, we filter out the blanks.
		Lastly, we group the words, aggregate them and drain the results to a Map. 
     * */
	private Pipeline pipeline_ReadFromDistributedList_WriteToDistributedMap() {
        Pipeline p = Pipeline.create();
        //p.drawFrom(Sources.<String> list(LIST_NAME)) // api antiguo
        p.readFrom(Sources.<String>list(LIST_NAME))
            .flatMap(sentence -> traverseArray(sentence.toLowerCase().split("\\W+"))) // word es sentence más bien
            .filter(word -> !word.isEmpty())
            .groupingKey(wholeItem())
            .aggregate(counting())
            //.drainTo(Sinks.map(MAP_NAME)); // api antiguo
            .writeTo(Sinks.map(MAP_NAME));
        return p;
    }
    
	
	// ------------------------------------------
	

    /** a job for executing the pipeline.
     * */
    public Long countWord(List<String> sentences, String word) {
        long count = 0;
        // We create a Jet instance first in order to create our job and use the pipeline.
        JetInstance jet = Jet.newJetInstance();
        
    	// Next, we copy the input List to a distributed list so that it's available over all the instances.
        List<String> textList = jet.getList(LIST_NAME);
        textList.addAll(sentences);
        
        // We then submit a job using the pipeline that we have built. The method newJob() returns an executable job that is started by Jet asynchronously.
        // The join method waits for the job to complete and throws an exception if the job is completed with an error.
        Pipeline p = pipeline_ReadFromDistributedList_WriteToDistributedMap();
        jet.newJob(p)
            .join();
        
        // When the job completes the results are retrieved in a distributed Map, as we defined in our pipeline. So, we get the Map from the Jet instance and get the counts of the word against it.
        Map<String, Long> counts = jet.getMap(MAP_NAME);
        count = counts.get(word);
        
        return count;
    }

}
