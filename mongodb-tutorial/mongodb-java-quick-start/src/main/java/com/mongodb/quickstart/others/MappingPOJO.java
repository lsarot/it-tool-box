package com.mongodb.quickstart.others;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.quickstart.initial.ConnectionStringInit;
import com.mongodb.quickstart.models.Grade;
import com.mongodb.quickstart.models.Score;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;
import static java.util.Collections.singletonList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


/**
 * maps Documents to POJOs, and CRUD examples
 * 
 * MongoDB is a dynamic schema database which means your documents can have different schemas within a single collection. 
 * Mapping all the documents from such a collection can be a challenge. So, sometimes, using the "old school" method and the Document class will be easier.
 * */
public class MappingPOJO {

    public static void main(String[] args) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        String connectionString = ConnectionStringInit.CONN_URI;
        
        //I need a ConnectionString instance instead of the usual String
        ConnectionString connString = new ConnectionString(connectionString);
        //I need to configure the CodecRegistry to include a codec to handle the translation to and from BSON for our POJOs
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        //And I need to add the default codec registry, which contains all the default codecs. They can handle all the major types in Java-like Boolean, Double, String, BigDecimal, etc
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                                                                .applyConnectionString(connString)
                                                                .codecRegistry(codecRegistry)
                                                                .build();
        
        
        try (MongoClient mongoClient = MongoClients.create(clientSettings)) {
            MongoDatabase db = mongoClient.getDatabase("sample_training");
            MongoCollection<Grade> grades = db.getCollection("grades", Grade.class);
            		//MongoCollection<Document> gradesCollection = sampleTrainingDB.getCollection("grades"); //notar que no es así esta vez!

            /**
             * notar que ahora trabajo con un POJO y no un Document
             * */
            // create a new grade.
            Grade newGrade = new Grade()
            							.setStudentId(10003d)
                                        .setClassId(10d)
                                        .setScores(singletonList(new Score().setType("homework").setScore(50d)));
            grades.insertOne(newGrade);
            System.out.println("Grade inserted.");

            
            // find this grade.
            Grade grade = grades.find(eq("student_id", 10003d)).first();
            System.out.println("Grade found:\t" + grade);

            
            // update this grade: adding an exam grade
            List<Score> newScores = new ArrayList<>(grade.getScores());
            newScores.add(new Score().setType("exam").setScore(42d));
            grade.setScores(newScores);
            
            Document filterByGradeId = new Document("_id", grade.getId());
            FindOneAndReplaceOptions returnDocAfterReplace = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
            Grade updatedGrade = grades.findOneAndReplace(filterByGradeId, grade, returnDocAfterReplace);
            System.out.println("Grade replaced:\t" + updatedGrade);

            
            // delete this grade
            System.out.println("Grade deleted:\t" + grades.deleteOne(filterByGradeId));
        }
    }
}
