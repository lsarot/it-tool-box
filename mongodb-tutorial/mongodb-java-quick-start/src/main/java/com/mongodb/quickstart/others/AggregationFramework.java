package com.mongodb.quickstart.others;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.quickstart.initial.ConnectionStringInit;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Accumulators.push;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;


/** AGGREGATIONS PIPELINES es la forma de trabajar con Java queries más complejas que conlleven funciones de agregación (group, sum, etc.)
 * 
 * The aggregation pipeline is a framework for data aggregation modeled on the concept of data processing pipelines, just like the "pipe" in the Linux Shell. 
 * Documents enter a multi-stage pipeline that transforms the documents into aggregated results.
 * 
 * It's the most powerful way to work with your data in MongoDB. It will allow us to make advanced queries like grouping documents, manipulate arrays, reshape document models, etc.
 * */
public class AggregationFramework {

    public static void main(String[] args) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        String connectionString = ConnectionStringInit.CONN_URI;
        
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase db = mongoClient.getDatabase("sample_training");
            MongoCollection<Document> zips = db.getCollection("zips");
            MongoCollection<Document> posts = db.getCollection("posts");
            
            threeMostPopulatedCitiesInTexas(zips);
            threeMostPopularTags(posts);
        }
    }

    
    /**
     * find the 3 most densely populated cities in Texas.
     * @param zips sample_training.zips collection from the MongoDB Sample Dataset in MongoDB Atlas.
     * 
     * En Compass, vamos a la pestaña Aggregations, y creamos nuestros Stages para ir trabajando la data sobre cada fase.
     * Los stages, de este aggregation pipeline, serían como en ** My Pipeline (al fondo)
     */
    private static void threeMostPopulatedCitiesInTexas(MongoCollection<Document> zips) {
    	
    	//así lo entrega Compass
    	List<Bson> aggregations = Arrays.asList(match(eq("state", "TX")), group("$city", sum("totalPop", "$pop")), project(fields(excludeId(), include("totalPop"), computed("city", "$_id"))), sort(descending("totalPop")), limit(3));
    	
    	//Así lo dejamos desglosado
        Bson match = match(eq("state", "TX"));
        Bson group = group("$city", sum("totalPop", "$pop"));
        Bson project = project(fields(excludeId(), include("totalPop"), computed("city", "$_id")));
        Bson sort = sort(descending("totalPop"));
        Bson limit = limit(3);

        List<Document> results = zips.aggregate(Arrays.asList(match, group, project, sort, limit))
                                     .into(new ArrayList<>());
        
        System.out.println("==> 3 most densely populated cities in Texas");
        results.forEach(printDocuments());
    }
    

    /**
     * find the 3 most popular tags and their post titles
     * @param posts sample_training.posts collection from the MongoDB Sample Dataset in MongoDB Atlas.
     * 
     * Here I'm using the very useful $unwind stage to break down my array of tags.
	 * It allows me in the following $group stage to group my tags, count the posts and collect the titles in a new array titles.
     */
    private static void threeMostPopularTags(MongoCollection<Document> posts) {
        Bson unwind = unwind("$tags");
        Bson group = group("$tags", sum("count", 1L), push("titles", "$title")); //podemos usar addToSet si no queremos que se repitan los posts
        Bson sort = sort(descending("count"));
        Bson limit = limit(3);
        Bson project = project(fields(excludeId(), computed("tag", "$_id"), include("count", "titles")));

        List<Document> results = posts.aggregate(Arrays.asList(unwind, group, sort, limit, project)).into(new ArrayList<>());
        
        System.out.println("==> 3 most popular tags and their posts titles");
        results.forEach(printDocuments());
    }

    private static Consumer<Document> printDocuments() {
        return doc -> System.out.println(doc.toJson(JsonWriterSettings.builder().indent(true).build()));
    }
    
}


/** My Pipeline:

//STAGE 1
[{$match: 
{
  state: "TX"
}}, 
//STAGE 2
{$group:
{
  _id: "$city",
  totalPop: {
    $sum: "$pop"
  }
}},
//STAGE 3
{$project: 
{
  _id: 0, //dont show
  totalPop: 1, //show
  city: "$_id" //show as
}},
//STAGE 4
{$sort: 
{
  totalPop: -1 //descending
}},
//STAGE 5
{$limit: 
3
}]
 * */

