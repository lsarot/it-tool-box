package com.cassandra.quickstart.initial;

import java.net.InetSocketAddress;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;

/** https://docs.datastax.com/en/developer/java-driver/4.7/manual/core/
 * 
    1. CqlSession is the main entry point of the driver. It holds the known state of the actual Cassandra cluster, and is what you use to execute queries. It is thread-safe, you should create a single instance (per target Cassandra cluster), and share it throughout your application;
    2. we use execute to send a query to Cassandra. This returns a ResultSet, which is an iterable of Row objects. On the next line, we extract the first row (which is the only one in this case);
    3. we extract the value of the first (and only) column from the row.
    
    We can use *Async variants of API methods that return a CompletionStage.
    
    If you don’t specify any contact point, the driver defaults to 127.0.0.1:9042:
    		CqlSession session = CqlSession.builder().build();

 * */
public class Connection {
	
    private static CqlSession session;
    
    //As soon as there are explicit contact points, you also need to provide the name of the local datacenter. All contact points must belong to it (as reported in their system tables: system.local.data_center and system.peers.data_center).
    static {
    	session = CqlSession.builder()
    	
    			// AWS KEYSPACES
    					//no ha funcionado!... en log4j2 sí funcionó, pudiéramos revisar cómo conecta internamente... pudiera faltarnos el ssl... no indagamos más por el momento!
    			//.addContactPoint(new InetSocketAddress("cassandra.us-east-1.amazonaws.com", 9142))
    		    //.withLocalDatacenter("us-east-1") // SELECT * FROM system.local			probar con data_center:		'us-east-1'	,	'Amazon Keyspaces'
    		    			// 'Test Cluster' nombre inventado sacado de clusterName de la config del appender de log4j2 funciona para log4j
    			//.withKeyspace(CqlIdentifier.fromCql("logger_messages_test"))
    		    //.withAuthCredentials("keyspaces-user.20200629-111000-at-462238265653", "...")
    			
    		    // EMBEDDED SERVER FOR TESTING PURPOSES
    			.addContactPoint(new InetSocketAddress("127.0.0.1", 9142))
    			.withLocalDatacenter("datacenter1")
    		    
    			.build();
    	
    	
    	// Anti-pattern: creating two sessions doubles the number of TCP connections opened by the driver
    	//CqlSession session1 = CqlSession.builder().withKeyspace(CqlIdentifier.fromCql("ks1")).build();
    	//CqlSession session2 = CqlSession.builder().withKeyspace(CqlIdentifier.fromCql("ks2")).build();
    }
    
    /*ON CONFIG FILE
     * // Add `application.conf` to your classpath with the following contents:
			datastax-java-driver {
			  basic {
			    contact-points = [ "1.2.3.4:9042", "5.6.7.8:9042" ]
			    load-balancing-policy.local-datacenter = datacenter1
			    
			    session-keyspace = my_keyspace //default keyspace if not specified on each query
			  }
			}
     */
	
    public static CqlSession getSession() {
    	return session;
    }
	
	public static void close() {
        session.close();
        //session.closeAsync();
        //session.forceCloseAsync();
    }
	
	//NOT RECOMMENDED AS EACH SESSION CONSUMES RESOURCES SUCH AS THREAD POOLS, TCP CONNS, ETC
	/*public void openSessionPerQuery() {
		try (CqlSession session = CqlSession.builder().build()) {
			  ResultSet rs = session.execute("select release_version from system.local");
			  Row row = rs.one();
			  System.out.println(row.getString("release_version"));
			}
	}*/
}



/* FOR 3.x DRIVER

public class Connection {
	 
    private Cluster cluster;
    private Session session;
 
    public void connect(String node, Integer port) {
        Builder b = Cluster.builder().addContactPoint(node);
        if (port != null) {
            b.withPort(port);
        }
        cluster = b.build();
 
        session = cluster.connect();
    }
 
    public Session getSession() {
        return this.session;
    }
 
    public void close() {
        session.close();
        cluster.close();
    }
}
*/