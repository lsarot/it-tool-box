package com.example.codigosbasicos.logging.log4j2.mongodb_conn_factory;

import java.util.Arrays;

import org.apache.logging.log4j.mongodb3.LevelCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDbConnectionFactory {

	/** HOW TO USE DATASTAX DRIVER TO ACCESS CASSANDRA
	 * https://docs.datastax.com/en/developer/java-driver/4.7/manual/core/
	 * https://mongodb.github.io/mongo-java-driver/3.12/driver/
	 * 
	 * MONGODB ATLAS USA 4.2 EN LA FREE-TIER, la de pago sí se puede cambiar la versión!
	 * log4j-mongodb3   usa driver 3.12.1, nada antiguo!.. debería servir la sintáxis del 3.7+ (existe la 4.0 ya) (2020-07-01) 
	 * puedo devolver MongoClient o MongoDatabase. El MongoDbProvider.class convierte a MongoDatabase el client
	 * 
	 * A MongoClient instance represents a pool of connections to the database; you will only need one instance of class MongoClient even with multiple threads.
	 * 
	 * WE HAVE TO ADD OUR IP ADDRESS TO ATLAS IP WHITELIST
	 * 
	 * podemos ver qué hace el log4j2 appender
	 * org.apache.logging.log4j.mongodb3.MongoDbProvider
	 * */
	/* https://docs.mongodb.com/manual/core/security-users/#authenticate-a-user
	 * 
	 * To authenticate as a user, you must provide a username, password, and the authentication database associated with that user.
	 * To authenticate using the mongo shell, either:
	 *     -Use the mongo command-line authentication options (--username, --password, and --authenticationDatabase) when connecting to the mongod or mongos instance, or
	 *     -Connect first to the mongod or mongos instance, and then run the authenticate command or the db.auth() method against the authentication database.
	 *     
	 * For users created in MongoDB, MongoDB stores all user information, including name, password, and the user's authentication database, in the system.users collection in the admin database.
	 * 			Do not access this collection directly but instead use the user management commands.    
	 * */
	public static MongoDatabase getNewMongoClient() {
		MongoClient mongoClient = null;
		MongoDatabase database = null;
		boolean mustClose = true;
		try {
			//driver 3.7+ (estos ejemplos de conexión los daba Atlas Cloud)
			//FUNCIONA
			//mongoClient = MongoClients.create("mongodb+srv://leo:test123mongo@freecluster-lmrqj.mongodb.net/logger_messages_test?retryWrites=true&w=majority"
			//		+ "&replicaSet=FreeCluster-shard-0&authSource=admin&ssl=true");      //&readPreference=primary (en Compass lo usa)
			
			
		  	//Conn string para otros drivers más antiguos (lo pegamos aquí pq nos ayuda a ver nodos y demás):   mongodb://leo:test123mongo@freecluster-shard-00-00-lmrqj.mongodb.net:27017,freecluster-shard-00-01-lmrqj.mongodb.net:27017,freecluster-shard-00-02-lmrqj.mongodb.net:27017/logger_messages_test?ssl=true&replicaSet=FreeCluster-shard-0&authSource=admin&retryWrites=true&w=majority
			//FUNCIONA
			MongoCredential credential = MongoCredential.createCredential("leo", "admin", "test123mongo".toCharArray()); //admin es la bbdd donde se guardan los usuarios!
			MongoClientSettings settings = MongoClientSettings.builder()
			        .credential(credential)
			        .applyToSslSettings(builder -> builder.enabled(true))
			        //.codecRegistry(pojoCodecRegistry) //lo vinculamos abajo de otra manera
			        .applyToClusterSettings(builder -> 
			            builder.hosts(Arrays.asList(
			            		new ServerAddress("freecluster-shard-00-00-lmrqj.mongodb.net", 27017),
                                new ServerAddress("freecluster-shard-00-01-lmrqj.mongodb.net", 27017),
                                new ServerAddress("freecluster-shard-00-02-lmrqj.mongodb.net", 27017))))
			        .build();
			mongoClient = MongoClients.create(settings);
		  	
			
			
			/* Una opción con un listener raro (no se probó!)
			ConnectionString connectionString = new ConnectionString("mongodb://host1:27107,host2:27017/?ssl=true");
		    CommandListener myCommandListener = new CommandListener() {..}};
		    MongoClientSettings settings = MongoClientSettings.builder()
		            .addCommandListener(myCommandListener)
		            .applyConnectionString(connectionString)
		            .build();
		    mongoClient = MongoClients.create(settings);
		    */
		
			
							//-------------------------------------------------
							//3.6+
							/*
							MongoClientURI uri = new MongoClientURI(
							    "mongodb+srv://leo:test123mongo@freecluster-lmrqj.mongodb.net/logger_messages_test?retryWrites=true&w=majority");
							mongoClient = new MongoClient(uri);
							*/
					
							//3.4	+	
							/*
							MongoClientURI uri = new MongoClientURI(
							    "mongodb://leo:test123mongo@freecluster-shard-00-00-lmrqj.mongodb.net:27017,freecluster-shard-00-01-lmrqj.mongodb.net:27017,freecluster-shard-00-02-lmrqj.mongodb.net:27017/logger_messages_test?ssl=true&replicaSet=FreeCluster-shard-0&authSource=admin&retryWrites=true&w=majority");
							mongoClient = new MongoClient(uri);
							*/
							
							//3.3-   (This driver version is not supported for MongoDB 3.4 and later. We recommend upgrading your driver to version 3.6 or above.)
							/*
							MongoClientURI uri = new MongoClientURI(
							    "mongodb://leo:test123mongo@freecluster-shard-00-00-lmrqj.mongodb.net:27017,freecluster-shard-00-01-lmrqj.mongodb.net:27017,freecluster-shard-00-02-lmrqj.mongodb.net:27017/logger_messages_test?ssl=true&replicaSet=FreeCluster-shard-0&authSource=admin&retryWrites=true&w=majority");
							mongoClient = new MongoClient(uri);
							*/
							//-------------------------------------------------
			
			
			
			//// CODEC REGISTRY (to interpret some classes) ////
			// http://mongodb.github.io/mongo-java-driver/3.8/driver/getting-started/quick-start-pojo/
			//Can't find a codec for class org.apache.logging.log4j.mongodb3.MongoDbDocumentObject (cuando logeo un throwable directamente y no su mensaje interno!)
			//LA SOLUCIÓN RÁPIDA: podemos configurar un mapeo para para que Mongo entienda como convertir una clase Pojo al hacer insert.
					//pero no podemos modificar la clase de la exception arrojada!!!
					//lo más simple es buscar un toString que represente al objeto!!!
			//Before you can use a POJO with the driver, you need to configure the CodecRegistry to include a codecs to handle the translation to and from bson for your POJOs. The simplest way to do that is to use the PojoCodecProvider.builder() to create and configure a CodecProvider.
			//The following example will combine the default codec registry, with the PojoCodecProvider configured to automatically create PojoCodecs, and LevelCodec for Log4j2 Level class.
			CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(
					MongoClientSettings.getDefaultCodecRegistry(),
					CodecRegistries.fromCodecs(new LevelCodec()),
					org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()) //must be last line if automatic(true)
					);
								
				
			database = mongoClient.getDatabase("logger_messages_test")
					.withCodecRegistry(pojoCodecRegistry);
					//or on collection:   collection.withCodecRegistry(pojoCodecRegistry);
		     
		    String collName = database.listCollectionNames().first(); // Check if the database actually requires authentication
		    
		    mustClose = false;
		    return database;
		     
	    } catch (final Exception e) {
	    	return null;
	    } finally {//entra antes del return o del catch
	    	if (mustClose) {
	    		System.out.println("ERROR CONNECTING TO MONGODB SERVER !!!");
	    		if (mongoClient != null) { mongoClient.close(); }
	            System.exit(-1); //NO DEBO ARRANCAR APLICACIÓN SI NO PUEDO CONECTAR A LA BBDD
	        }
	    }
	}
	
}
