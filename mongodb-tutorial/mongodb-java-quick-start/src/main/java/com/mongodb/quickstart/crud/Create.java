package com.mongodb.quickstart.crud;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.quickstart.initial.ConnectionStringInit;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.asList;

/**
 * every write operation (create, replace, update, delete) performed on a SINGLE document is ACID in MongoDB. 
 * Which means insertMany is not ACID by default but, good news, since MongoDB 4.0, we can wrap this call in a multi-document ACID transaction to make it fully ACID
 * */
public class Create {

    private static final Random rand = new Random();

    public static void main(String[] args) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        String connectionString = ConnectionStringInit.CONN_URI;
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {

            MongoDatabase sampleTrainingDB = mongoClient.getDatabase("sample_training");
            MongoCollection<Document> gradesCollection = sampleTrainingDB.getCollection("grades");

            insertOneDocument(gradesCollection);
            insertManyDocuments(gradesCollection);
        }
    }

    /**
     * insert one
     * */
    private static void insertOneDocument(MongoCollection<Document> gradesCollection) {
        gradesCollection.insertOne(generateNewGrade(10000d, 1d));
        System.out.println("One grade inserted for studentId 10000.");
    }

    
    /**
     * insert many
     * */
    private static void insertManyDocuments(MongoCollection<Document> gradesCollection) {
        List<Document> grades = new ArrayList<>();
        for (double classId = 1d; classId <= 10d; classId++) {
            grades.add(generateNewGrade(10001d, classId));
        }

        gradesCollection.insertMany(grades, new InsertManyOptions().ordered(false));//usar false si queremos que no se detenga si alguna inserción falla!.. ya que son independientes cada una.. si alguna falla, arroja exception, pero al final!
        System.out.println("Ten grades inserted for studentId 10001.");
    }

    
    private static Document generateNewGrade(double studentId, double classId) {
        List<Document> scores = asList(new Document("type", "exam").append("score", rand.nextDouble() * 100),
                                       new Document("type", "quiz").append("score", rand.nextDouble() * 100),
                                       new Document("type", "homework").append("score", rand.nextDouble() * 100),
                                       new Document("type", "homework").append("score", rand.nextDouble() * 100));
        return new Document("_id", new ObjectId())
        										.append("student_id", studentId)
                                                .append("scores", scores)
                                                .append("class_id", classId);
    }
}
