package com.cassandra.quickstart.initial;

import java.io.IOException;

import org.apache.cassandra.exceptions.ConfigurationException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;


/** FOR SPRING DATA WITH CASSANDRA
 * https://www.baeldung.com/spring-data-cassandra-tutorial
 * 
 * https://docs.datastax.com/en/developer/java-driver/4.7/manual/
 * */
/* CONSIDERAR EN CASSANDRA:
 * Cassandra se diseña pensando en las queries de tu aplicación
 * Pueden haber varias tablas casi idénticas, donde los datos son los mismos, pero una tabla está optimizada para una consulta y otra para otra.
 * Entonces, debemos hacer inserts en múltiples tablas para no perder la consistencia.
 * Tenemos una consulta tipo BATCH
			 BEGIN BATCH
			 insert into ... ;
			 insert into ... ;
			 APPLY BATCH;
	Note: As of version 3.0, a new feature called “Materialized Views” is available , which we may use instead of batch queries.
			https://www.datastax.com/blog/2015/06/new-cassandra-30-materialized-views
 * */
public class HelloCassandra {

	public static void main(String[] args) {
		
		//NOS APOYAMOS EN UN EMBEDDED SERVER QUE TRAE org.cassandraunit:cassandra-unit ARTIFACT
		try {
			EmbeddedCassandraServerHelper.startEmbeddedCassandra();
		} catch (ConfigurationException | IOException | InterruptedException e) {
			e.printStackTrace();
		}

		
		CqlSession session = Connection.getSession();

		
		//KEYSPACE USAGE
				//don't change at runtime, better to prefix on queries
				//Be very careful though: switching the key-space at runtime is inherently thread-unsafe, so if the session is shared by multiple threads (and is usually is), it could easily cause unexpected query failures.
		//session.execute("SELECT * FROM logger_messages_test.logs"); // WILL ALWAYS WORK
		//session.execute("USE my_keyspace");
		//session.execute("SELECT * FROM logs"); // Now the key-space is set, unqualified query works
		//on Driver 4+
		//Can't use per-request keyspace with protocol V4 (thrown)
		//ResultSet rs =  session.execute(SimpleStatement.newInstance("SELECT * FROM logs")
				      //.setKeyspace(CqlIdentifier.fromCql("logger_messages_test")));
		
		
		//CREATING KEYSPACE
		session.execute("CREATE KEYSPACE IF NOT EXISTS \"logger_messages_test\" with replication={'class':'SimpleStrategy','replication_factor': 1}");
		
		//CREATING COLUMN FAMILY (table)
		session.execute("CREATE TABLE IF NOT EXISTS \"logger_messages_test\".\"logs\"(\"level\" text, \"id\" timeuuid, \"timeid\" timeuuid, \"message\" text, \"marker\" text, \"logger\" text, \"timestamp\" timestamp, \"mdc\" map<text, text>, \"ndc\" list<text>, PRIMARY KEY(\"level\", \"id\")) WITH CLUSTERING ORDER BY(\"id\" DESC)");
		
		
		//INSERTING DATA
		String level = "INFO";
		String msg = "INFO message to log!.";
		String logger = "com.cassandra.quickstart.initial.HelloCassandra";
		java.time.Instant timestamp = java.time.Instant.now();
		PreparedStatement ps = session.prepare("insert into logger_messages_test.logs "
				+ "(id, level, logger, timeid, timestamp, message) "
				+ "values (?, ?, ?, ?, ?, ?)");
		
					//ps.bind().setString("s", "324378").setString("d", "LCD screen")			//si usamos values (:s, :d)
					//ps.boundStatementBuilder().setString(0, "324378").setString(1, "LCD screen")			//por posición
					//ps.bind().setString("sku", "324378").setString("description", "LCD screen")				//también reconoce las columnas por nombre si usamos  ?
		
		BoundStatement bound = ps.bind(com.datastax.oss.driver.api.core.uuid.Uuids.timeBased(), level, logger, com.datastax.oss.driver.api.core.uuid.Uuids.timeBased(), timestamp, msg);
		
		session.execute(bound);
		
		
		//RETRIEVING DATA
		ResultSet rs0 = session.execute("SELECT * FROM logger_messages_test.logs;");
		
		rs0.forEach(row-> {
			//d82c8970-c185-11ea-b497-d121cd19ab3c INFO com.cassandra.quickstart.initial.HelloCassandra d82cfea0-c185-11ea-b497-d121cd19ab3c 2020-07-09T01:45:18.935Z INFO message to log!.
			System.out.println(row.getUuid("id") + " " + row.getString("level") + " " + row.getString("logger") + " " + row.getUuid("timeid") + " " + row.getInstant("timestamp") + " " + row.getString("message"));
			for (ColumnDefinition def : row.getColumnDefinitions()) {
			    System.out.printf("%s; %s; %s; %s%n", def.getKeyspace(), def.getTable(), def.getName(), def.getType());
			}
		});
		
		
		
					ResultSet rs = session.execute("Select * from system_schema.keyspaces;");
					rs.forEach(row-> {
						for (ColumnDefinition def : row.getColumnDefinitions()) {
						    System.out.printf("%s; %s; %s; %s%n", def.getKeyspace(), def.getTable(), def.getName(), def.getType());
						}
					});

		
		
		//This will return all results without limit (even though the driver might use multiple queries in the background). To handle large result sets, you might want to use a LIMIT clause in your CQL query, or use one of the techniques described in the paging documentation.
		Row r = rs0.one();
		for (Row row : rs0) {
			//String firstName = row.getString(0);
			//String firstName = row.getString(CqlIdentifier.fromCql("first_name")); //CqlIdentifier is a convenient wrapper that deals with case-sensitivity
			//String firstName = row.getString("first_name");
			
			
			//NULL VALUES
			//For performance reasons, the driver uses primitive Java types wherever possible (boolean, int…); the CQL value NULL is encoded as the type’s default value (false, 0…), which can be ambiguous. To distinguish NULL from actual values, use isNull:
			//Integer age = row.isNull("age") ? null : row.getInt("age");
			
			
			//COLLECTION TYPES
			//To ensure type safety, collection getters are generic. You need to provide type parameters matching your CQL type when calling the methods:
			//List<String> givenNames = row.getList("given_names", String.class);
			
			//For nested collections, element types are generic and cannot be expressed as Java Class instances. Use GenericType instead:
			//GenericType<Set<List<String>>> listOfStrings = new GenericType<Set<List<String>>>() {};
			//Set<List<String>> teams = row.get("teams", listOfStrings);
			//Since generic types are anonymous inner classes, it’s recommended to store them as constants in a utility class instead of re-creating them each time.
			
			
			//ROW METADATA
			//ResultSet and Row expose an API to explore the column metadata at runtime:
			//for (ColumnDefinition def : row.getColumnDefinitions()) {
			    //System.out.printf("%s; %s; %s; %s%n", def.getKeyspace(), def.getTable(), def.getName(), def.getType());
			//}
		}
		
		
		//STATEMENT BUILDER
		/*
		SimpleStatement statement = SimpleStatement.builder("SELECT * FROM foo")
			    .setPageSize(20)
			    .setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM)
			    .setIdempotence(true)
			    .build();
		*/
		
		
		//PREPARED STATEMENT
		//PreparedStatement preparedFindById = session.prepare("SELECT * FROM user WHERE id = ?");
		//Row row = session.execute(preparedFindById.bind("100")).one();

		
		
		//REMEMBER TO RELEASE RESOURCES AT THE END
		Connection.close();
		EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
		
	}
	
}
