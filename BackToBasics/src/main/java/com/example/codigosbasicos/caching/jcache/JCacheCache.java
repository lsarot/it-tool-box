package com.example.codigosbasicos.caching.jcache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryUpdatedListener;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import javax.cache.spi.CachingProvider;

import com.example.codigosbasicos.caching.jcache.JCacheCache.SimpleCacheLoader;

/** https://redisson.org/glossary/jcache.html

JCache is the standard caching API for the Java programming language. Developers can use JCache to temporarily cache Java objects using the CachingProvider interface.
JCache is formally known as "JSR107," a Java Specification Request that describes a certain part of the Java platform.

The JCache API is located in the javax.cache package. It consists of five core interfaces:

	    CachingProvider:	The CachingProvider interface oversees the operation of one or more CachingManagers. You can use multiple CachingProviders as necessary.
	    CachingManager:	The CachingManager interface is responsible for establishing, acquiring, managing, and configuring one or more Caches. In turn, these CachingManagers are controlled by a CachingProvider. 
	    Cache: 					The Cache interface is exactly what it sounds like: a cache for temporarily storing Java objects. More specifically, Caches are map data structures in which key-value pairs are stored. Each Cache can only be controlled by one CachingManager.
	    Entry: 						The Entry interface designates a single key-value pair in a Cache.
	    ExpiryPolicy: 			The ExpiryPolicy defines the length of time with which a single Java object is stored in a Cache. Once this time elapses, the object "expires" and can no longer be accessed.
		Configuration:			Is an interface that enables us to configure Caches. It has one concrete implementation – MutableConfiguration and a subinterface – CompleteConfiguration.

While these five interfaces provide a general structure for JCache APIs, they do not implement it. Instead, any technology that implements a JCache API must adhere to the defined requirements.

Outside these requirements, different JCache implementations can make different assumptions. For example, although JCache requires Caches to use a key-value structure, it does not describe how objects in this format should be stored. One implementation may optimize JCache for space efficiency, while another may decide to lower response times.

This freedom is perhaps most noticeable when JCache implementations choose between the "store by value" and "store by reference" paradigms:

	    Store by value: 		The Cache makes a copy of the object being inserted in the cache, and consults this copy rather than the original object.
	    Store by reference: The Cache only holds a link to the original object, which is not stored in the cache. This saves on space requirements, but comes with the risk that the original objects will change during the lifetime of the Cache.

		Estas deben ser las weakkeys, weakvalues y softvalues que hacen mención otras librerías locales.
		NO, eso hace ref al garbage-collector que podrá recuperar memoria si no existen referencias a los objetos de la caché.

Since JCache is a specification and not an implementation, developers have multiple versions to choose from, including:

	    The JCache reference implementation		- Note that the JCache reference implementation is not intended for production use - only as a proof of concept. For example, it lacks features and optimizations such as tiered storage and distributed caching.
	    Caffeine
	    Hazelcast
	    Terracotta Ehcache
	    Oracle Coherence
	    Infinispan
	    Redis (Redisson)

		If we do not provide any implementation of JCache in our pom.xml, the following exception will be thrown:
				javax.cache.CacheException: No CachingProviders have been configured

 * */
/* JCache and Redis

Redis is an open-source, in-memory data structure store often used to implement key-value NoSQL databases and caches. Although Redis isn't compatible with Java out of the box, Java developers who use Redis can make use of a third-party Redis Java client such as Redisson.

The Redisson client includes an implementation of the JCache API for Redis. Below is a simple demonstration of how to use JCache with Redis and Redisson, loading either a JSON or YAML configuration file:

	MutableConfiguration<String, String> config = new MutableConfiguration<>();
	
	// YAML configuration
	URI redissonConfigUri = getClass().getResource("redisson-jcache.yaml").toURI();

	CacheManager manager = Caching.getCachingProvider().getCacheManager(redissonConfigUri, null);
	Cache<String, String> cache = manager.createCache("namedCache", config);

Redisson passes all of the Technology Compatibility Kit (TCK) tests for JCache, making it a fully certified implementation.
Plus, if JCache isn't for you, Redisson also supports two other methods for distributed caching in Java: the Spring framework and the Map collection.
 * */
public class JCacheCache {

	/**
	 * https://www.baeldung.com/jcache
	 * */
	public static void main(String[] args) {
		new JCacheCache().run();
	}

	private void run() {
		//BASIC USAGE
		CachingProvider cachingProvider = Caching.getCachingProvider();
		CacheManager cacheManager = cachingProvider.getCacheManager();
		MutableConfiguration<String, String> config = new MutableConfiguration<>();
		Cache<String, String> cache = cacheManager.createCache("simpleCache", config);
		cache.put("key1", "value1");
		cache.put("key2", "value2");
		cacheManager.close();
		
		
		System.out.println("************************************* Entry processor *************************************");
		//allows us to modify Cache entries using atomic operations without having to re-add them to the Cache. To use it, we need to implement the EntryProcessor interface:
		cache.invoke("key",new SimpleEntryProcessor());
		cache.get("key");// == "value - modified"
		
		
		System.out.println("************************************* Synchronous Loading *************************************");
		synchronousLoading();
		
		
		System.out.println("************************************* Event Listeners *************************************");
		//triggering any of the event types defined in the EventType enum. created, updated, removed, expired
		eventListeners();
		
	}
	
	
	public static class SimpleEntryProcessor implements EntryProcessor<String, String, String>, Serializable {   
	    public String process(MutableEntry<String, String> entry, Object... args) throws EntryProcessorException {
	        if (entry.exists()) {
	            String current = entry.getValue();
	            entry.setValue(current + " - modified");
	            return current;
	        }
	        return null;
	    }
	}
	
	
	public static class SimpleCacheEntryListener implements CacheEntryCreatedListener<String, String>, CacheEntryUpdatedListener<String, String>, Serializable {
	    public void onUpdated(
	    		Iterable<CacheEntryEvent<? extends String,? extends String>> events) throws CacheEntryListenerException {
	    	System.out.println("onUpdated");
	    }
	    public void onCreated(
	    		Iterable<CacheEntryEvent<? extends String,? extends String>> events) throws CacheEntryListenerException {
	    	System.out.println("onCreated");
	    }
	}
	
	
	public static class SimpleCacheLoader implements CacheLoader<Integer, String> {
	 
	    public String load(Integer key) throws CacheLoaderException {
	        return "fromCache" + key;
	    }
	    
	    public Map<Integer, String> loadAll(Iterable<? extends Integer> keys)
	      throws CacheLoaderException {
	        Map<Integer, String> data = new HashMap<>();
	        for (int key : keys) {
	            data.put(key, load(key));//retrieve from
	        }
	        return data;
	    }
	}
	
	
	private void eventListeners() {
		CachingProvider cachingProvider = Caching.getCachingProvider();
		CacheManager cacheManager = cachingProvider.getCacheManager();
		MutableConfiguration<String, String> config = new MutableConfiguration<>();
		Cache<String, String> cache = cacheManager.createCache("simpleCache", config);
		cache.put("key1", "value1");
		
		SimpleCacheEntryListener listener = new SimpleCacheEntryListener();
		MutableCacheEntryListenerConfiguration listenerConfiguration
				= new MutableCacheEntryListenerConfiguration<String, String>(FactoryBuilder.factoryOf(listener), null, false, true);
	    cache.registerCacheEntryListener(listenerConfiguration);
	    cache.put("key", "value");
	    cache.put("key", "newValue");
	}

	
	private void synchronousLoading() {
		CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        //MutableConfiguration<Integer, String> config = new MutableConfiguration<>()
            //.setReadThrough(true)
            
        	//NO SE LOGRÓ CONFIGURAR!
        	//.setCacheLoaderFactory(new SimpleCacheLoader());
            //.setCacheLoaderFactory(FactoryBuilder.SingletonFactory<CacheLoader<Integer,String>>(new SimpleCacheLoader()));
            //.setCacheLoaderFactory(new FactoryBuilder.SingletonFactory<SimpleCacheLoader>(new SimpleCacheLoader()));
        
        
        //this.cache = cacheManager.createCache("SimpleCache", config);
	}
	
}
