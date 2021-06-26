package com.example.leo.redisdemo;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.redisson.Redisson;
import org.redisson.RedissonMultiLock;
import org.redisson.api.*;
import org.redisson.api.RScript.Mode;
import org.redisson.client.RedisClient;
import org.redisson.client.RedisClientConfig;
import org.redisson.client.RedisConnection;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.config.Config;

import com.example.leo.redisdemo.util.CustomMessage;
import com.example.leo.redisdemo.util.Ledger;
import com.example.leo.redisdemo.util.LedgerLiveObject;
import com.example.leo.redisdemo.util.LedgerServiceImpl;
import com.example.leo.redisdemo.util.LedgerServiceInterface;
import com.example.leo.redisdemo.util.LedgerServiceInterfaceAsync;

/** REDIS GUIDE WITH REDISSON
 * https://www.baeldung.com/redis-redisson
 * https://github.com/redisson/redisson
 * https://github.com/redisson/redisson/wiki/
 * https://redis.io/topics/quickstart
 * 
 * 
 * Redisson constitutes an in-memory data grid that offers distributed Java objects and services backed by Redis. Its distributed in-memory data model allows sharing of domain objects and services across applications and servers.
 * Redisson facilitates building distributed business applications.
 * 
 * Redisson supports connections to the following Redis configurations:
	    Single node
	    Master with slave nodes
	    Sentinel nodes
	    Clustered nodes
	    Replicated nodes
	    * Redisson supports AWS ElastiCache Cluster and Azure Redis Cache for Clustered and Replicated Nodes.
	    
 * Redisson also provides integration with other frameworks such as the JCache API, Spring Cache, Hibernate Cache and Spring Sessions.
 * https://github.com/redisson/redisson/wiki/14.-Integration-with-frameworks
 * 
 * */
public class RedisDemo {

	// Sync and Async API
	RedissonClient client;
	// Reactive API
	RedissonReactiveClient clientReactive;
	// RxJava2 API
	RedissonRxClient clientRx;
			
			
	public static void main(String[] args) {
		new RedisDemo().run();
	}
	
	private void run() {
		configuration();
		operation();
		objects();
		collections();
		locksAndSynchronyzers();
		services();
		pipelining();
		//luaScripting();
		//lowLevelClient();
		
		//dummiesPerformanceTest();
	}

	private void configuration() {
		// 1. Create config object
		
		//RedissonClient client = Redisson.create(); // localhost:6379
		
		// JAVA CONFIG
		Config config = new Config();
		config
			.useSingleServer()
				.setAddress("redis://127.0.0.1:6379")
			//.useMasterSlaveServers()
			//.useSentinelServers()
			//.useClusterServers()
				//.addNodeAddress("redis://127.0.0.1:7181"); // use "rediss://" for SSL connection
			//.useReplicatedServers()
			;
		
		
		// FROM CONFIG FILE
		// from a string, file, input stream or URL
		//Config config = 
				//Config.fromJSON(new File("singleNodeConfig.json"))
				//config.fromYAML(new File("config-file.yaml"))
				// tip: environmental vars should be wrapped: ${REDIS_PORT} ; address: "redis://127.0.0.1:${REDIS_PORT:-6379}" (default if not set)
		
		
		// Retrieve config as String in accepted formats
		try {
			//String jsonFormat = config.toJSON(); // deprecated! *
			String yamlFormat = config.toYAML();
			System.out.println(yamlFormat);
		} catch (IOException e) {e.printStackTrace();}
		
		
		// 2. Create Redisson instance

		// :::: Sync and Async API
		client = Redisson.create(config);

		// :::: Reactive API
		clientReactive = Redisson.createReactive(config);

		// :::: RxJava2 API
		clientRx = Redisson.createRx(config);
		
		
		// 3. now use the client... there are over 50 Redis based Java objects and services ...
		
		// Redisson supports synchronous, asynchronous and reactive interfaces.
		// Operations over these interfaces are thread-safe.
	}
	
	
	private void operation() {
		RAtomicLong myLong = client.getAtomicLong("myLong");
		
		RFuture<Boolean> isSet = myLong.compareAndSetAsync(6, 27);
		
		// We can set listeners on this object to get back the result when it becomes available:
        isSet.handle((result, exception) -> { // handle the result or exception here.
            if (exception != null) {
                String msg = exception.getMessage();
                try {
                    throw new Exception("operation ... failed. Exception msg: " + msg, exception.getCause());
                } catch (Exception e) {
                    //logger.warn("Exception caught in addRedisUser: {}", e.getMessage());
                }
            } else {
            	System.out.println("myLong successfully set!");
            }
            return result;
        });
		
        
        // :::: Reactive client
        RAtomicLongReactive myLongReactive = clientReactive.getAtomicLong("myLong");
		Publisher<Boolean> isSetPublisher = myLongReactive.compareAndSet(5, 28); // This method returns reactive objects based on the Reactive Streams Standard for Java 9.
		
	}
	
	
	/** An individual instance of a Redisson object is serialized and stored in any of the available Redis nodes backing Redisson. These objects could be distributed in a cluster across multiple nodes and can be accessed by a single application or multiple applications/servers.
		These distributed objects follow specifications from the java.util.concurrent.atomic package. They support lock-free, thread-safe and atomic operations on objects stored in Redis. 
		Data consistency between applications/servers is ensured as values are not updated while another application is reading the object.
		Redisson objects are bound to Redis keys. We can manage these keys through the RKeys interface. And then, we access our Redisson objects using these keys.
	 * */
	private void objects() {
		//There are several options we may use to get the Redis keys.

		//We can simple get all the keys:
		RKeys keys = client.getKeys();

		//Alternatively, we can extract only the names:
		Iterable<String> allKeys = keys.getKeys();

		//And finally, we're able to get the keys conforming to a pattern:
		Iterable<String> keysByPattern = keys.getKeysByPattern("key*");
		
		//keys.deleteAsync(keys..)

		//allKeys.forEach(k -> System.out.println(k));
		
		
		
		/* Distributed objects provided by Redisson include:
		    ObjectHolder
		    BinaryStreamHolder
		    GeospatialHolder
		    BitSet
		    AtomicLong
		    AtomicDouble
		    Topic
		    BloomFilter
		    HyperLogLog
		 * */
		
		
		// :::: Object Holder

		//Represented by the RBucket class, this object can hold any type of object. This object has a maximum size of 512MB:
		//The RBucket object can perform atomic operations such as compareAndSet and getAndSet on objects it holds.

		RBucket<Ledger> bucket = client.getBucket("ledger");
		bucket.set(new Ledger());
		Ledger ledger = bucket.get();

		
		// :::: AtomicLong

		//Represented by the RAtomicLong class, this object closely resembles the java.util.concurrent.atomic.AtomicLong class and represents a long value that can be updated atomically:

		RAtomicLong atomicLong = client.getAtomicLong("myAtomicLong");
		atomicLong.set(5);
		atomicLong.incrementAndGet();

		
		// :::: Topic

		//The Topic object supports the Redis' “publish and subscribe” mechanism. To listen for published messages:

		RTopic subscribeTopic = client.getTopic("topic");
		subscribeTopic.addListener(CustomMessage.class, (channel, customMessage) -> 
			System.out.println(customMessage.getMessage())
		);

		//Above, the Topic is registered to listen to messages from the “baeldung” channel. We then add a listener to the topic to handle incoming messages from that channel. We can add multiple listeners to a channel.
		//Let's publish messages to the “baeldung” channel:
		//This could be published from another application or server. The CustomMessage object will be received by the listener and processed as defined in the onMessage method.

		RTopic publishTopic = client.getTopic("topic");
		RFuture<Long> clientsReceivedMessage = publishTopic.publishAsync(new CustomMessage("This is a message"));
		//SI PUBLICO DESDE OTRA APP SYNC O ASYNC, DICE QUE LO RECIBIÓ N CLIENTES, PERO RARA VEZ SE EJECUTÓ EL println EN LA CONSOLA
	}

	
	private void collections() {
		/* Distributed collections provided by Redisson include:
		    Map
		    Multimap
		    Set
		    SortedSet
		    ScoredSortedSet
		    LexSortedSet
		    List
		    Queue
		    Deque
		    BlockingQueue
		    BoundedBlockingQueue
		    BlockingDeque
		    BlockingFairQueue
		    DelayedQueue
		    PriorityQueue
		    PriorityDeque
		 * */
		
		
		// :::: Map

		//Redisson based maps implement the java.util.concurrent.ConcurrentMap and java.util.Map interfaces. Redisson has four map implementations. These are RMap, RMapCache, RLocalCachedMap and RClusteredMap.
		//RMapCache supports map entry eviction. RLocalCachedMap allows local caching of map entries. RClusteredMap allows data from a single map to be split across Redis cluster master nodes.
		//client.getMapCache("").expireAt(...)

		RMap<String, Ledger> map = client.getMap("ledgerMap");
		Ledger newLedger = map.put("123", new Ledger());

		//map.clear();
		//map.delete();
		//map.unlink();
				
		
		// :::: Set

		//Redisson based Set implements the java.util.Set interface.
		//Redisson has three Set implementations, RSet, RSetCache, and RClusteredSet with similar functionality as their map counterparts.

		RSet<Ledger> ledgerSet = client.getSet("ledgerSet");
		ledgerSet.add(new Ledger());

		
		// :::: List

		//Redisson-based Lists implement the java.util.List interface.

		RList<Ledger> ledgerList = client.getList("ledgerList");
		ledgerList.add(new Ledger());
	}
	
	
	/** Redisson's distributed locks allow for thread synchronization across applications/servers
	 * */
	private void locksAndSynchronyzers() {
		/* Redisson's list of locks and synchronizers include:
		    Lock
		    FairLock
		    MultiLock
		    ReadWriteLock
		    Semaphore
		    PermitExpirableSemaphore
		    CountDownLatch
		 * */
		
		
		// :::: Lock

		//Redisson's Lock implements java.util.concurrent.locks.Lock interface.

		RLock lock = client.getLock("lock");
		lock.lock();
		// perform some long operations...
		lock.unlock();

		
		// :::: MultiLock

		//Redisson's RedissonMultiLock groups multiple RLock objects and treats them as a single lock:

		RLock lock1 = client.getLock("lock1");
		RLock lock2 = client.getLock("lock2");
		//RLock lock3 = clientInstance3.getLock("lock3"); // even from another connection
		 
		RedissonMultiLock mlock = new RedissonMultiLock(lock1, lock2);
		mlock.lock();
		// perform long running operation...
		mlock.unlock();
	}
	
	
	private void services() {
		/* Redisson exposes 4 types of distributed services:
			Remote Service
			Live Object Service
			Executor Service
			Scheduled Executor Service
		 * */
		
		
		// :::: Remote Service (RMI)

		//This service provides Java remote method invocation facilitated by Redis. A Redisson remote service consists of a server-side (worker instance) and client-side implementation. The server-side implementation executes a remote method invoked by the client. Calls from a remote service can be synchronous or asynchronous.

		//The server-side registers an interface for remote invocation:
		RRemoteService remoteService = client.getRemoteService();
		LedgerServiceInterface ledgerServiceImpl = new LedgerServiceImpl();
		//remoteService.register(LedgerServiceInterface.class, ledgerServiceImpl);
		remoteService.register(LedgerServiceInterface.class, ledgerServiceImpl, 3, Executors.newFixedThreadPool(3));
		//SE PUEDE REGISTRAR LA INTERFAZ N VECES (en otros servidores) Y LA SUMA DE TODOS LOS WORKERS SERÁ EL TOTAL DISPONIBLE PARA EJECUCIÓN PARALELA

		
					//NO FUNCIONA DESDE OTRA APP LA INVOCACIÓN, incluso configurando timeouts
					// 1 second ack timeout and 30 seconds execution timeout
					//RemoteInvocationOptions options = RemoteInvocationOptions.defaults();
					// no ack but 30 seconds execution timeout
					//RemoteInvocationOptions options = RemoteInvocationOptions.defaults().noAck();
					// 1 second ack timeout then forget the result
					//RemoteInvocationOptions options = RemoteInvocationOptions.defaults().noResult();
					// 10 seconds ack timeout then forget about the result
					//RemoteInvocationOptions options = RemoteInvocationOptions.defaults().expectAckWithin(10, TimeUnit.SECONDS).noResult();
					// no ack and forget about the result (fire and forget)
					//RemoteInvocationOptions options = RemoteInvocationOptions.defaults().noAck().noResult();
					// 5 seconds ack, 5 seconds for response
					RemoteInvocationOptions options = RemoteInvocationOptions.defaults().expectAckWithin(5000).expectResultWithin(5000);
		
					//The client-side calls a method of the registered remote interface:
					RRemoteService remoteService2 = client.getRemoteService();
					LedgerServiceInterface ledgerService = remoteService2.get(LedgerServiceInterface.class, options);
					List<String> entries = ledgerService.getEntries(10);
					entries.forEach(e -> System.out.println(e));
					
					//ASYNC RMI
					LedgerServiceInterfaceAsync ledgerServiceA = remoteService2.get(LedgerServiceInterfaceAsync.class, options);
					RFuture<List<String>> res = ledgerServiceA.getEntries(10);
					res.thenApply(list -> {
						list.forEach(e -> System.out.println(e));
						return true;
					});
					
		
		// :::: Live Object Service

		//Redisson Live Objects extend the concept of standard Java objects that could only be accessed from a single JVM to enhanced Java objects that could be shared between different JVMs in different machines. This is accomplished by mapping an object's fields to a Redis hash. This mapping is made through a runtime-constructed proxy class. Field getters and setters are mapped to Redis hget/hset commands.
		//Redisson Live Objects support atomic field access as a result of Redis' single-threaded nature.

		RLiveObjectService service = client.getLiveObjectService();
		LedgerLiveObject ledger = new LedgerLiveObject();
		ledger.setName("ledger1");
		try {
			ledger = service.persist(ledger);
		} catch (Exception e) {e.printStackTrace();}

					//We create our Live Object like standard Java objects using the new keyword. We then use an instance of RLiveObjectService to save the object to Redis using its persist method.
					//If the object has previously been persisted to Redis, we can retrieve the object:
					//We use the RLiveObjectService to get our Live Object using the field annotated with @RId.
					LedgerLiveObject returnLedger = service.get(LedgerLiveObject.class, "ledger1");
	}
	
	
	/** Multiple operations can be batched as a single atomic operation
	 * */
	private void pipelining() {
		RBatch batch = client.createBatch();
		batch.getMap("ledgerMap").fastPutAsync("1", "2");
		batch.getMap("ledgerMap").putAsync("2", "5");

		BatchResult<?> batchResult = batch.execute();
	}
	
	
	private void luaScripting() {
		client.getBucket("foo").set("bar");
		String result = client.getScript().eval(
				Mode.READ_ONLY,
				"return redis.call('get', 'foo')", 
				RScript.ReturnType.VALUE);
	}
	
	
	/** Redisson provides a low-level client that allows execution of native Redis commands
	 *  also supports asynchronous operations.
	 * */
	private void lowLevelClient() {
		RedisClientConfig redisClientConfig = new RedisClientConfig();
		redisClientConfig.setAddress("localhost", 6379);
		 
		RedisClient client = RedisClient.create(redisClientConfig);
		 
		RedisConnection conn = client.connect();
		conn.sync(StringCodec.INSTANCE, RedisCommands.SET, "test", 0);
		 
		conn.closeAsync();
		client.shutdown();
	}
	
	
	/**
	 * insertamos secuencial, no es lo habitual
	 * recuperamos random, es lo habitual
	 * */
	private void dummiesPerformanceTest() {
		System.out.println("Dummies Performance Test");
		long init = System.currentTimeMillis();
		
				//Probar quizás con:
				//client, reactiveClient
				//getMap   (interminable, rápido los primeros 1.6MM en 30seg (60MB), luego parece que el map no progresaba, debe ser debido a las claves secuenciales, ie. si usa un BTree todo irá a la derecha y no estará distribuido)
				//getBucket   (1000seg)
				//lowLevelClient   (1000seg)
				//createBatch, batch.getMap, batch.execute
		
		
		//RMap<String, String> map = client.getMap("testMap");
		//map.clear();
		//map.delete();
		//map.unlink();
		//System.out.println(map.size() + "   " + map.sizeInMemory() + "   " + map.isEmpty());
		
		
		/*RedisClientConfig redisClientConfig = new RedisClientConfig();
		redisClientConfig.setAddress("localhost", 6379); 
		RedisClient client = RedisClient.create(redisClientConfig);
		RedisConnection conn = client.connect();*/
		
		
		
		for (int i = 0; i < 5000000; i++) {
			//map.fastPutAsync("key-"+i, "abcdefghijklmnopqrstuvwxyz"); // 90B c/u. Nunca terminó, se estancó en 60MB de tamaño el proceso redis-server
			RBucket<String> bucket = client.getBucket("key-"+i); // 600MB total (125B c/u), quizás un Map está más optimizado!
				bucket.set("abcdefghijklmnopqrstuvwxyz");
			//conn.sync(StringCodec.INSTANCE, RedisCommands.SET, "key-"+i, "abcdefghijklmnopqrstuvwxyz");
			
			if (i % 200000 == 0) {System.out.println("->"+i);}
		}
		for (int i = 0; i < 5000000; i++) {
			//map.get("key-"+ThreadLocalRandom.current().nextInt(0, 5000000 + 1));
			RBucket<String> bucket = client.getBucket("key-"+i); // secuencial para ir viendo avance cada n retrieved
				bucket.get();
			//conn.sync(StringCodec.INSTANCE, RedisCommands.GET, "key-"+i);
			
			if (i % 200000 == 0) {System.out.println("->"+i);}
		}
		System.out.println("Dummy test took ms : " + (System.currentTimeMillis()-init)); // 1.000 seg y usando RBucket o LowLevelClient... Usando RMap no terminaba nunca!.. Cache2K y Caffeine demoran 15seg.
		
		//conn.closeAsync();
		//client.shutdown();
	}
	
}
