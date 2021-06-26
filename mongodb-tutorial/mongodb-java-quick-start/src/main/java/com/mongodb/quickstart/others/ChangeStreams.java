package com.mongodb.quickstart.others;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.quickstart.initial.ConnectionStringInit;
import com.mongodb.quickstart.models.Grade;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.changestream.FullDocument.UPDATE_LOOKUP;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/** Change Streams 
 * were introduced in MongoDB 3.6. They allow applications to access real-time data changes without the complexity and risk of tailing the oplog.
 * Applications can use change streams to subscribe to all data changes on a single collection, a database, or an entire deployment, and immediately react to them. 
 * Because change streams use the aggregation framework, an application can also filter for specific changes or transform the notifications at will.
 * 
 * NOS PERMITEN IR RECIBIENDO NOTIFICACIONES DE CAMBIOS SOBRE UNA COLLECTION POR EJEMPLO, filtrando tipos de cambios y reaccionando ante estos cambios en tiempo real.
 * 
 * 
 * NOTA SOBRE USO EN PRODUCCIÓN:
 * Change Streams are very easy to use and setup in MongoDB. They are the key to any real-time processing system.
 * The only remaining problem here is how to get this in production correctly. Change Streams are basically an infinite loop, processing an infinite stream of events. Multiprocessing is, of course, a must-have for this kind of setup, especially if your processing time is greater than the time separating 2 events.
 * Scaling up correctly a Change Stream data processing pipeline can be tricky. That's why you can implement this easily using MongoDB Triggers in MongoDB Stitch.
 * O debemos usar varios threads capaces de procesar la enorme cantidad de eventos que pudieran llegar al stream.
 * */
public class ChangeStreams {

    public static void main(String[] args) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        String connectionString = ConnectionStringInit.CONN_URI;
        
        ConnectionString connString = new ConnectionString(connectionString);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                                                                .applyConnectionString(connString)
                                                                .codecRegistry(codecRegistry)
                                                                .build();

        try (MongoClient mongoClient = MongoClients.create(clientSettings)) {
            MongoDatabase db = mongoClient.getDatabase("sample_training");
            MongoCollection<Grade> grades = db.getCollection("grades", Grade.class);
            List<Bson> pipeline;


            //// HAY 5 EJEMPLOS 
            //// * no es necesario mapear a POJOs, ver nota en Example 4!
            //// *** Only uncomment one example at a time. Follow instructions for each individually then kill all remaining processes.

            
            /** => Example 1: print all the write operations.
             *  => Start "ChangeStreams" then "MappingPOJOs" to see some change events.
             */
            grades.watch().forEach(printEvent());
		            //otra forma de escribirlo, pero usamos printEvent() para reutilizarlo en los otros ejemplos.
		            //ChangeStreamIterable<Grade> changeStream = grades.watch();
		            //changeStream.forEach((Consumer<ChangeStreamDocument<Grade>>) System.out::println);

            
            /** => Example 2: print only insert and delete operations.
             *  => Start "ChangeStreams" then "MappingPOJOs.java" to see some change events.
             */
            // pipeline = singletonList(match(in("operationType", asList("insert", "delete"))));
            // grades.watch(pipeline).forEach(printEvent());

            
            /** => Example 3: print only updates without fullDocument.
             *  => Start "ChangeStreams" then "Update.java" to see some change events (start "Create" before if not done earlier).
             */
            // pipeline = singletonList(match(eq("operationType", "update")));
            // grades.watch(pipeline).forEach(printEvent());

            
            /** => Example 4: print only updates with fullDocument
             * ... to avoid overloading the stream, Mongo by default doesn't show the updates Document, but we can change this behavior.
             *  => Start "ChangeStreams" then "Update.java" to see some change events.
             */
            /** Note: The Update.java program updates a made-up field "comments" that doesn't exist in my POJO Grade which represents the original schema for this collection. Thus the field doesn't appear in the output as it's not mapped.
             * If I want to see this comments field, I can use a MongoCollection not mapped automatically to my Grade.java POJO.
             * 
             * MongoCollection<Document> grades = db.getCollection("grades"); 
             * instead of 
             * MongoCollection<Grade> grades = db.getCollection("grades", Grade.class);
             * */
            // pipeline = singletonList(match(eq("operationType", "update")));
            // grades.watch(pipeline).fullDocument(UPDATE_LOOKUP).forEach(printEvent());

            
            /** CHANGE STREAMS ARE RESUMABLE:
             * It's important to note that a change stream will resume itself automatically in the face of an "incident". Generally, the only reason that an application needs to restart the change stream manually from a resume token is if there is an incident in the application itself rather than the change stream (e.g. an operator has decided that the application needs to be restarted).
             * 
             * => Example 5: iterating using a cursor and a while loop + remembering a resumeToken then restart the Change Streams.
             * ... will simulate that an error occurred, just by iterating over the events until a predefined point (until we get to num 8), then will resume processing events on the stream that weren't flushed yet.
             * => Start "ChangeStreams" then "Update.java" to see some change events.
             */
            // exampleWithResumeToken(grades);
        }
    }

    
    private static void exampleWithResumeToken(MongoCollection<Grade> grades) {
        List<Bson> pipeline = singletonList(match(eq("operationType", "update")));
        ChangeStreamIterable<Grade> changeStream = grades.watch(pipeline);
        MongoChangeStreamCursor<ChangeStreamDocument<Grade>> cursor = changeStream.cursor();
        System.out.println("==> Going through the stream a first time & record a resumeToken");
        int indexOfOperationToRestartFrom = 5;
        int indexOfIncident = 8;
        int counter = 0;
        BsonDocument resumeToken = null;
        while (cursor.hasNext() && counter != indexOfIncident) {
            ChangeStreamDocument<Grade> event = cursor.next();
            if (indexOfOperationToRestartFrom == counter) {
                resumeToken = event.getResumeToken();
            }
            System.out.println(event);
            counter++;
        }
        System.out.println("==> Let's imagine something wrong happened and I need to restart my Change Stream.");
        System.out.println("==> Starting from resumeToken=" + resumeToken);
        assert resumeToken != null;
        grades.watch(pipeline).resumeAfter(resumeToken).forEach(printEvent());
    }
    

    private static Consumer<ChangeStreamDocument<Grade>> printEvent() {
        return System.out::println;
    }
}
