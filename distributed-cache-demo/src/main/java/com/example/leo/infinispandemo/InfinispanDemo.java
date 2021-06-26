package com.example.leo.infinispandemo;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.*;

import com.example.leo.infinispandemo.util.CacheListener;
import com.example.leo.infinispandemo.util.Util;


/** INFINISPAN
 * an in-memory key/value data store that ships with a more robust set of features than other tools of the same niche.
 * 
 * Key features:
 * 		PASSIVATION
 * 		TRANSACTION
 * 
 * Para guardar en disco mejor usamos LevelDB, ha sido más rápido considerablemente !!!
 * */
public class InfinispanDemo {

	private Cache<String, String> simpleCache;
	private Cache<String, String> expirationCache;
	private Cache<String, String> evictingCache;
	private Cache<String, String> passivationCache;
	private Cache<String, Integer> transactionalCache;
	private Cache<String, String> dummiesTestCache;
	
	
	public static void main(String[] args) {
		new InfinispanDemo().run();
	}

	private void run() {
		configuration();
		usage();
		
		dummiesPerformanceTest();
	}

	private void configuration() {
		// we'll cover 5 diff configs
		simpleCache();
		expirationCache();
		evictingCache();
		passivationCache();
		transactionalCache();
		dummiesTestConfig();
	}

	
	private void simpleCache() {
		simpleCache = this.buildCache(
				"SIMPLE_CACHE", cacheManager(), new CacheListener(),
				new ConfigurationBuilder()
				.build());
	}
	
	public String findSimpleCacheValue(String key) {
	    return simpleCache.computeIfAbsent(key, k -> Util.getFromDatabase(k));
	}
	
	
	private void expirationCache() {
		expirationCache = this.buildCache(
				"EXPIRATION_CACHE", cacheManager(), new CacheListener(),
				new ConfigurationBuilder().expiration()
			    .lifespan(10, TimeUnit.SECONDS)
			    .build());
	}
	
	
	private void evictingCache() {
		evictingCache = this.buildCache(
				"EVICTING_CACHE", cacheManager(), new CacheListener(),
				new ConfigurationBuilder()
				.memory()
				.maxCount(1)
				//.maxSize("10")
				//.evictionType(EvictionType.COUNT).size(1)  *** deprecated, pero explica dif entre maxCount y maxSize
				.build());
	}
	
	
	/** The cache passivation is one of the powerful features of Infinispan.
	 * By combining passivation and eviction, we can create a cache that doesn't occupy a lot of memory, without losing information.
	 * */
	private void passivationCache() {
		passivationCache = this.buildCache(
				"PASSIVATION_CACHE", cacheManager(), new CacheListener(),
				new ConfigurationBuilder()
			      .memory()
			      .maxCount(1)
				  //.maxSize("10")
			      .persistence() 
			      .passivation(true) // activating passivation
			      .addSingleFileStore() // in a single file
			      .purgeOnStartup(true) // clean the file on startup
			      .location(new File("/Users/Leo/infinispan").getAbsolutePath()) 
			      .build());
	}
	
	
	/** Infinispan ships with a powerful transaction control. Like the database counterpart, it is useful in maintaining integrity while more than one thread is trying to write the same entry.
	 * */
	private void transactionalCache() {
		transactionalCache = this.buildCache(
				"TRANSACTIONAL_CACHE", cacheManager(), new CacheListener(),
				new ConfigurationBuilder()
				.transaction()
				.transactionMode(TransactionMode.TRANSACTIONAL)
			    .lockingMode(LockingMode.PESSIMISTIC)
				.build());
	}
	
	
	private void dummiesTestConfig() {
		dummiesTestCache = this.buildCache(
				"DUMMIES_TEST_CACHE", cacheManager(), null,
				new ConfigurationBuilder()
			      .memory()
			      .maxCount(2000000)
				  //.maxSize("10")
			      .persistence() 
			      .passivation(true) // activating passivation
			      .addSingleFileStore() // in a single file
			      .purgeOnStartup(true) // clean the file on startup
			      .location(new File("/Users/Leo/infinispan").getAbsolutePath()) 
			      .build());
	}
	
	
	private void usage() {
		String key = "anyKey";
		
		// SIMPLE CACHE WITH LOADER FUNCTION
		long time = Util.timeThis(() -> findSimpleCacheValue(key));
		String value = simpleCache.computeIfAbsent(key, k -> Util.getFromDatabase(k));
		
		
		// EXPIRING CACHE WITH MANUAL INSERTION
		value = expirationCache.get(key);
	    if (value == null) {
	        value = Util.getFromDatabase(key);
	        expirationCache.put(key, value);
	    }
	    //set expiration even when not in main configuration
	    expirationCache.put(key, value, 10, TimeUnit.SECONDS);
	    //or fixed lifespan
	    //using -1 to the lifespan attribute, the cache won't suffer expiration from it, but when we combine it with 10 seconds of idleTime, we tell Infinispan to expire this entry unless it is visited in this timeframe.
	    expirationCache.put(key, value, -1, TimeUnit.SECONDS, 10, TimeUnit.SECONDS);
	    
	    
	    // EVICTING CACHE
	    value = evictingCache.computeIfAbsent(key, k -> Util.getFromDatabase(k));
	    value = evictingCache.computeIfAbsent("anotherKey", k -> Util.getFromDatabase(k));
	    value = evictingCache.computeIfAbsent(key, k -> Util.getFromDatabase(k));
		
	    
	    // PASSIVATION CACHE
	    value = passivationCache.computeIfAbsent(key, k -> Util.getFromDatabase(k));
	    value = passivationCache.computeIfAbsent("anotherKey", k -> Util.getFromDatabase(k));
	    value = passivationCache.computeIfAbsent(key, k -> Util.getFromDatabase(k)); 
	    
	    
	    // TRANSACTIONAL CACHE
	    //we'll test with 2 methods, one quick and the other slower
	    try {
	    	Runnable backGroundJob = () -> {
				try {transactionSlow();} catch (Exception e) {e.printStackTrace();}
			};
	    	Thread backgroundThread = new Thread(backGroundJob);
			transactionQuick();
			backgroundThread.start();
			Thread.sleep(100); //lets wait our thread warm up
			transactionQuick(); // should see on the console that it lasted some time because the other was blocking
	    } catch (Exception e) {e.printStackTrace();}
	}
	
	
	public Integer transactionQuick() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException, NotSupportedException {
	    TransactionManager tm = transactionalCache.getAdvancedCache().getTransactionManager();
	    tm.begin();
	    Integer howManyVisits = transactionalCache.get("count")  == null ? 0 : 1;
	    howManyVisits++;
	    System.out.println("I'll try to set HowManyVisits to " + howManyVisits);
	    long time = System.currentTimeMillis();
	    transactionalCache.put("count", howManyVisits);
	    time = System.currentTimeMillis() - time;
	    System.out.println("Quick Tx -> I was able to set HowManyVisits to " + howManyVisits + " after waiting " + time + " ms");
	    tm.commit();
	    return howManyVisits;
	}

	public void transactionSlow() throws NotSupportedException, SystemException {
	    TransactionManager tm = transactionalCache.getAdvancedCache().getTransactionManager();
	    tm.begin();
	    transactionalCache.put("count", 1000);
	    System.out.println("HowManyVisits should now be 1000, but we are holding the transaction");
	    try {Thread.sleep(1000L);} catch (InterruptedException e) {	e.printStackTrace();}
	    tm.rollback();
	    System.out.println("The slow tx suffered a rollback");
	}
	
	
	/** The CacheManager is the foundation of the majority of features that we'll use. It acts as a container for all declared caches, controlling their lifecycle, and is responsible for the global configuration.
	 * */
	public DefaultCacheManager cacheManager() {
	    return new DefaultCacheManager();
	}
	
	private <K, V> Cache<K, V> buildCache(
			String cacheName, 
			DefaultCacheManager cacheManager, 
			CacheListener listener,
			Configuration configuration) {

		cacheManager.defineConfiguration(cacheName, configuration);
		Cache<K, V> cache = cacheManager.getCache(cacheName);
		if (listener != null) {
			cache.addListener(listener);
		}
		return cache;
	}
	
	
	/**
	 * insertamos secuencial, no es lo habitual
	 * recuperamos random, es lo habitual
	 * */
	private void dummiesPerformanceTest() {
		System.out.println("Dummies Performance Test");
		long init = System.currentTimeMillis();
		
		for (int i = 0; i < 5000000; i++) {
			dummiesTestCache.put("key-"+i, "abcdefghijklmnopqrstuvwxyz");
			if (i % 200000 == 0) {System.out.println("->"+i);}
		}
		for (int i = 0; i < 5000000; i++) {
			dummiesTestCache.get("key-"+i);
			if (i % 200000 == 0) {System.out.println("->"+i);}
		}
		System.out.println("Dummy test took ms : " + (System.currentTimeMillis()-init));
		// 20 seg con 5MM capacity
		// 530 seg usando 2MM capacity y el resto en fichero, 1.85 GB ram se lleva otro proceso que levanta la librería, y unos 93MB por cada 100mil en fichero (parece que al terminar el proceso también manda lo de la ram a ficheros)
	}
	
}
