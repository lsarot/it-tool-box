package com.mongodb.quickstart.crud;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.quickstart.initial.ConnectionStringInit;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;

public class Delete {

    public static void main(String[] args) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        String connectionString = ConnectionStringInit.CONN_URI;
        
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase sampleTrainingDB = mongoClient.getDatabase("sample_training");
            MongoCollection<Document> gradesCollection = sampleTrainingDB.getCollection("grades");

            // delete one document
            Bson filter = eq("student_id", 10000);
            DeleteResult result = gradesCollection.deleteOne(filter);
            System.out.println(result);

            
            //You are emotionally attached to your document and you want a chance to see it one last time before it's too late?
            // findOneAndDelete operation
            filter = eq("student_id", 10002);
            Document doc = gradesCollection.findOneAndDelete(filter);
            System.out.println(doc.toJson(JsonWriterSettings.builder().indent(true).build()));

            
            // delete many documents
            filter = gte("student_id", 10000);
            result = gradesCollection.deleteMany(filter);
            System.out.println(result);

            
            // delete the entire collection and its metadata (indexes, chunk metadata, etc).
            //gradesCollection.drop();
        }
    }
}
