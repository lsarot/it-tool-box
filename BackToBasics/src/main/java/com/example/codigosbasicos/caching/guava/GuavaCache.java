package com.example.codigosbasicos.caching.guava;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.example.codigosbasicos.caching.Employee;
import com.example.codigosbasicos.caching.FakeDatabase;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Guava y las demás soportan básicamente las mismas funciones.
 * en este ejemplo de Guava no se muestran a fondo todas las características
 * */
public class GuavaCache {

	public static void main(String[] args) {
		new GuavaCache().run();
	}
	
	private void run() {
		//create a cache for employees based on their employee id
		LoadingCache<String, Employee> cache = 
				CacheBuilder.newBuilder()
				.maximumSize(100)                             // maximum 100 records can be cached
				.expireAfterAccess(30, TimeUnit.MINUTES)      // cache will expire after 30 minutes of access
				.build(new CacheLoader<String, Employee>() {  // build the cacheloader

					//the main idea is that if it isn't on cache, load method will be called. So we will populate one at a time, not the full data set.
					@Override
					public Employee load(String empId) throws Exception {
						//make the expensive call
						return FakeDatabase.getFromDatabase(empId);
					} 
				});

		
		try {			
			System.out.println("Invocation #1... cache will be populated with corresponding employee record");
			System.out.println(cache.get("100"));
			System.out.println(cache.get("101"));
			System.out.println(cache.get("102"));
			System.out.println(cache.get("103"));
			System.out.println(cache.get("110"));
			System.out.println();
			System.out.println("Invocation #2... data will be returned from cache");
			System.out.println(cache.get("100"));
			System.out.println(cache.get("101"));
			System.out.println(cache.get("102"));
			System.out.println(cache.get("103"));
			System.out.println(cache.get("110"));

		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
