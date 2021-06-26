package com.example.codigosbasicos.caching.cache2k;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheEntry;
import org.cache2k.CacheManager;
import org.cache2k.configuration.Cache2kConfiguration;
import org.cache2k.configuration.Cache2kManagerConfiguration;
import org.cache2k.core.InternalCache;
import org.cache2k.core.InternalCacheInfo;
import org.cache2k.event.CacheEntryCreatedListener;

import com.example.codigosbasicos.caching.Employee;
import com.example.codigosbasicos.caching.FakeDatabase;

/** https://cache2k.org/docs/1.0/user-guide.html
 * */
public class Cache2kCache {

	public static void main(String[] args) {
		new Cache2kCache().run();
	}

	private void run() {
		dummiesPerformanceTest();
		
		System.out.println("************************************* Manual populating *************************************");
		manualPopulating();
		System.out.println("************************************* Synchronous Loading *************************************");
		synchronousLoading();
		/*
		System.out.println("************************************* Asynchronous Loading *************************************");
		asynchronousLoading();
		 */
		System.out.println("************************************* Eviction Strategies & Statistics (see the code) *************************************");
		//evictionStrategies();
		statistics();
		System.out.println("************************************* Event Listeners *************************************");
		eventListeners();
		
		//We also have methods like:   containsAndRemove, putIfAbsent, removeIfEquals, replaceIfEquals, peekAndReplace, and peekAndPut.
	}
	
	
	private void manualPopulating() {
		Cache<String, Employee> cache = Cache2kBuilder.of(String.class, Employee.class)
		          .name("employees")
		          .entryCapacity(100)
		          //.eternal(true)
		          .expireAfterWrite(1, TimeUnit.MINUTES)
		          .build();
		
		assert(cache.get("500") == null);
		cache.put("500", new Employee("Name","Dept","500"));
		assert(cache.get("500") != null);
		System.out.println(cache.get("500"));
		cache.remove("500");
		assert(cache.get("500") == null);
		
		cache.peek("500");//just searchs on cache
		
		cache.getEntry("500");//will use the loader if is not in cache
		String key = "50";
		Employee o = cache.computeIfAbsent(key, new Callable<Employee>() {
			@Override public Employee call() throws Exception {
				System.out.println("callable with key: "+key);
				return new Employee("name","dept",key);
			}});
		System.out.println(o);
	}
	
	
	private void synchronousLoading() {
		//DECLARA LAS CACHE GLOBALMENTE Y PUEDO RECUPERARLAS
		//Cache<String, Employee> cache = CacheManager.getInstance().getCache("employees");
		//CacheManager.getInstance().getActiveCaches()
		
		Cache<String, Employee> cache = Cache2kBuilder.of(String.class, Employee.class)
		          .name("employees2")
		          .entryCapacity(100)
		          //.eternal(true)
		          .expireAfterWrite(1, TimeUnit.MINUTES)
		          .loaderThreadCount(8)
		          //.loaderExecutor(v)
		          .loader(k -> FakeDatabase.getFromDatabase(k))
		          .build();
		
		Employee emp = cache.get("100");
		assert(emp != null);
		System.out.println(emp);
		
		//retrieve multiple items at once (can also receive a function)
		Map<String, Employee> items 
				= cache.getAll(Arrays.asList("101", "102", "103"));
		
		items.forEach((k,v) -> System.out.println(v) );
	}
	
	
	private void statistics() {
		Cache<String, Employee> cache = CacheManager.getInstance().getCache("employees2");
		
		cache.getAll(Arrays.asList("101", "103"));
		
		InternalCacheInfo stats = ((InternalCache)cache).getInfo();
		System.out.println("HitRate: " + stats.getHitRate());
		System.out.println("GetCount: " + stats.getGetCount());
		System.out.println("LoadMillis: " + stats.getLoadMillis());
		//...
	}
	
	
	private void eventListeners() {
		Cache<String, Employee> cache = Cache2kBuilder.of(String.class, Employee.class)
		          .name("employees3")
		          .entryCapacity(100)
		          .disableStatistics(true)
		          .expireAfterWrite(1, TimeUnit.MINUTES)
		          .loader(k -> FakeDatabase.getFromDatabase(k))
		          //event listeners execute synchronously except for the expiry events. If we want an asynchronous listener, we can use the addAsyncListener method.
		          .addAsyncListener(new CacheEntryCreatedListener<String, Employee>() {
		        	    @Override
		        	    public void onEntryCreated(Cache<String, Employee> cache, CacheEntry<String, Employee> entry) {
		        	    	System.out.println("Entry created: "+entry.getKey() + " - " + entry.getValue());
		        	    }
		        	})
		          .build();
		
		//retrieve multiple items at once (can also receive a function)
		Map<String, Employee> items 
				= cache.getAll(Arrays.asList("101", "102", "103"));
		
		items.forEach((k,v) -> System.out.println(v) );
	}
	
	
	/**
	 * insertamos secuencial, no es lo habitual
	 * recuperamos random, es lo habitual
	 * */
	private void dummiesPerformanceTest() {
		long init = System.currentTimeMillis();
		Cache<String, String> cache = Cache2kBuilder.of(String.class, String.class)
		          .name("dummy-test")
		          .entryCapacity(2000000)
		          //.eternal(true)
		          .expireAfterWrite(2, TimeUnit.MINUTES)
		          .build();
		
		for (int i = 0; i < 5000000; i++) {
			cache.put("key-"+i, "abcdefghijklmnopqrstuvwxyz");
		}
		for (int i = 0; i < 5000000; i++) {
			cache.peek("key-"+ThreadLocalRandom.current().nextInt(0, 5000000 + 1));
		}
		System.out.println("Dummy test took ms : " + (System.currentTimeMillis()-init)); // 15s con 5MM capacity, 30s con 2MM capacity 
	}
	
}
