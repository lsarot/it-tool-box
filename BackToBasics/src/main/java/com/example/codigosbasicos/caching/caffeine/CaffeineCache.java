package com.example.codigosbasicos.caching.caffeine;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.cache2k.Cache2kBuilder;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.example.codigosbasicos.caching.Employee;
import com.example.codigosbasicos.caching.FakeDatabase;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.github.benmanes.caffeine.cache.stats.ConcurrentStatsCounter;
import com.github.benmanes.caffeine.cache.stats.StatsCounter;

/** https://github.com/ben-manes/caffeine
 * 
 * Caffeine parece ser creada por quienes diseñaron Guava.
 * 
 * It is worth to mention that the underlying impl, most caching frameworks use, is a ConcurrentHashMap !
 * */

public class CaffeineCache {

	public static void main(String[] args) {
		new CaffeineCache().run();
	}

	private void run() {
		dummiesPerformanceTest();
		
		System.out.println("************************************* Manual populating *************************************");
		manualPopulating();
		System.out.println("************************************* Synchronous Loading *************************************");
		synchronousLoading();
		System.out.println("************************************* Asynchronous Loading *************************************");
		asynchronousLoading();
		System.out.println("************************************* Eviction Strategies & Statistics (see the code) *************************************");
		evictionStrategies();
		statistics();
	}


	/**
	 * Note we did not configure a method to load data on request in the cache building code
	 * */
	private void manualPopulating() {
		Cache<String, Employee> cache = Caffeine.newBuilder()
				  .expireAfterWrite(1, TimeUnit.MINUTES)
				  .maximumSize(100)
				  .build();
		
		//manual populating and invalidating
		assert(cache.getIfPresent("500") == null);
		cache.put("500", new Employee("Name","Dept","500"));
		assert(cache.getIfPresent("500") != null);
		System.out.println(cache.getIfPresent("500"));
		cache.invalidate("500");
		assert(cache.getIfPresent("500") == null);
		
		//then we pass a function to load if it is not cached
		Employee emp = cache.get("100", k -> 
				FakeDatabase.getFromDatabase(k));
		System.out.println(emp);
	}

	
	/**
	 * Note we did register a function to retrieve from source (on build method)
	 * */
	private void synchronousLoading() {
		LoadingCache<String, Employee> cache = Caffeine.newBuilder()
				  .maximumSize(10)
				  .expireAfterWrite(1, TimeUnit.MINUTES)
				  .build(k -> 
				  		FakeDatabase.getFromDatabase(k));
		
		Employee emp = cache.get("100");
		assert(emp != null);
		System.out.println(emp);
		
		//retrieve multiple items at once (can also receive a function)
		//los que no estén cacheados se cargarán con el loader, esto no es mucho más rápido que pedir uno a uno al cache. Ahora, supongamos que ninguno está en el cache, llamará N veces a la bbdd, es mejor 1 sólo request a la bbdd con los que no encontró
		Map<String, Employee> items 
				= cache.getAll(Arrays.asList("101", "102", "103"));
		
		items.forEach((k,v) -> System.out.println(v) );
		
		System.out.println(" *** Cache items count: " + cache.estimatedSize());//ESTIMATED PQ PUEDE CONTAR LOS QUE HAN SIDO GARBAGE COLLECTED
	}
	
	
	/**
	 * Works the same as the Synchronous, but performs operations asynchronously and returns a CompletableFuture holding the actual value
	 * */
	private void asynchronousLoading() {
		AsyncLoadingCache<String, Employee> cache = Caffeine.newBuilder()
				  .maximumSize(100)
				  .expireAfterWrite(1, TimeUnit.MINUTES)
				  .buildAsync(k -> {
					  System.out.println("in common loader");
					  return FakeDatabase.getFromDatabase(k);
				  	});
		
		//providing a particular loader method
		cache.get("100", k -> {
			System.out.println("in provided loader");
			return FakeDatabase.getFromDatabase(k);
		});
		
		//java.util.concurrent.ExecutorService e = Executors.newFixedThreadPool(5);
		cache.get("404", (k, executor) -> {
			executor.execute(()->{
				//some task... no sé su utilidad, un runnable (no callable) antes de insertar en la caché 
			});
			return null;
		});
		
		//We can use the get and getAll methods, in the same manner, taking into account the fact that they return CompletableFuture
		//so we can handle it later or register a listener
		
		cache.get("100").thenAccept(v -> {
		    assert(v != null);
		    System.out.println(v);
		});
		
		//get many at once, then call listener
		CompletableFuture cf = cache.getAll(Arrays.asList("101", "102", "103"))
				.thenAccept(map -> 
					map.forEach((k,v) -> System.out.println(v))
					);
		
		System.out.println("*** we continue running! ***");
		
		//this is synchronous
		try {
			System.out.println(cache.get("110").get());
		} catch (InterruptedException  | ExecutionException e) { e.printStackTrace(); }
		//this also
		//cache.synchronous().get(key)
		
		//CompletableFuture.allOf(cf).join();
		cf.join();

		
		//putting the value (several ways)
		Employee _e0 = new Employee("name", "dept", "id");
		cache.synchronous().put("key", _e0);
		
		CompletableFuture _cf0 = new CompletableFuture();
		_cf0.complete(_e0);
		cache.put("key", CompletableFuture.completedFuture(_e0)); //_cf0
	}
	
	
	/**
	 * size-based, time-based, and reference-based
	 * */
	private void evictionStrategies() {
		sizeBased();
		timeBased();
		referenceBased();
	}

	private void sizeBased() {
		//seteamos size o weight, no ambas!
		LoadingCache<String, Employee> cache = Caffeine.newBuilder()
				//.maximumSize(10) // BY ITEMS COUNT 
				.maximumWeight(15) // BY WEIGHT
				.weigher((k, v) -> {
					return 5;// retornamos el peso del objeto para ser computado el peso total
				})
				.build(k -> FakeDatabase.getFromDatabase(k));
		
		cache.cleanUp();//forces to apply eviction policies
	}
	
	private void timeBased() {
		//Expire after access — entry is expired after period is passed since the last read or write occurs
	    //Expire after write — entry is expired after period is passed since the last write occurs
	    //Custom policy — an expiration time is calculated for each entry individually by the Expiry implementation

		//after access y after write
		LoadingCache<String, Employee> cache = Caffeine.newBuilder()
				.expireAfterAccess(5, TimeUnit.MINUTES)
				.expireAfterWrite(10, TimeUnit.SECONDS)
				.refreshAfterWrite(1, TimeUnit.MINUTES)
						//difference between expireAfter and refreshAfter:
						//Si está expired, bloquea y retorna el valor más reciente.
						//Refresh devuelve el actual y luego buscará asíncronamente el valor más reciente.
				.build(k -> FakeDatabase.getFromDatabase(k));
	
		//custom policy (applies individually for each item)
		//pero, se refiere a millis, seconds o qué medida de tiempo ?
		cache = Caffeine.newBuilder().expireAfter(new Expiry<String, Employee>() {
			@Override
			public long expireAfterCreate(String key, Employee value, long currentTime) {
				return Integer.parseInt(value.getEmplD()) * 1000;
			}
			@Override
			public long expireAfterUpdate(String key, Employee value, long currentTime, long currentDuration) {
				return currentDuration;
			}
			@Override
			public long expireAfterRead(String key, Employee value, long currentTime, long currentDuration) {
				return currentDuration;
			}
		}).build(k -> FakeDatabase.getFromDatabase(k));
	}
	
	
	/** We can configure our cache to allow garbage-collection of cache keys and/or values.
	 * To do this, we'd configure usage of the WeakRefence for both keys and values,
	 * 			and we can configure the SoftReference for garbage-collection of values only.
	 * The WeakRefence usage allows garbage-collection of objects when there are not any strong references to the object.
	 * SoftReference allows objects to be garbage-collected based on the global Least-Recently-Used strategy of the JVM.
	 * */
	//es la politica de desalojo menos recomendada quizás!!!
	private void referenceBased() {
		//seteamos weak o soft values, no ambas!
		LoadingCache<String, Employee> cache = Caffeine.newBuilder()
				.weakKeys() // parece que compara claves usando ==. Esto quizás hace que "key"=="key" sea false al no ser el mismo objeto  y se llame nuevamente el loader ?. Dice que viola la definición de map si usas Cache.asMap(). 
				.weakValues() // similar pero con values. Afecta algunas funciones. This feature cannot be used in conjunction with buildAsync. consider softValues instead.
				.softValues() // Softly-referenced objects will be garbage-collected in a globally least-recently-used manner, in response to memory demand. This feature cannot be used in conjunction with buildAsync. in most circumstances it is better to set a per-cache maximum size instead of using soft references. You should only use this method if you are very familiar with the practical consequences of soft references.
				.build(k -> FakeDatabase.getFromDatabase(k));
	}
	
	
	//operación costosa. Usar para pruebas.
	private void statistics() {
		LoadingCache<String, Employee> cache = Caffeine.newBuilder()
				.recordStats()
				//.recordStats(supplier)
				.build(k -> FakeDatabase.getFromDatabase(k));
		
		//cache.stats().hitCount()
		//cache.stats().missCount()
		//cache.stats()... 	OTROS MÉTODOS
	}
	
	
	/**
	 * insertamos secuencial, no es lo habitual
	 * recuperamos random, es lo habitual
	 * */
	private void dummiesPerformanceTest() {
		long init = System.currentTimeMillis();
		Cache<String, String> cache = Caffeine.newBuilder()
				.maximumSize(2000000)
				.expireAfterWrite(2, TimeUnit.MINUTES)
				.build();
		
		for (int i = 0; i < 5000000; i++) {
			cache.put("key-"+i, "abcdefghijklmnopqrstuvwxyz");
		}
		for (int i = 0; i < 5000000; i++) {
			cache.getIfPresent("key-"+ThreadLocalRandom.current().nextInt(0, 5000000 + 1));
		}
		System.out.println("Dummy test took ms : " + (System.currentTimeMillis()-init)); // 15s con 5MM capacity, 9s con 2MM capacity (9s con 2MM pq tiene menos entradas donde buscar (en el indice) y usamos getIfPresent (no usa loader), pero Cache2k demoró 30 y usando método peek, equivale a getIfPresent) 
	}
	
}
