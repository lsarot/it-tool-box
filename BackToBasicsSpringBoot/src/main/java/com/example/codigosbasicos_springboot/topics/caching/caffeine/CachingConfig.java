package com.example.codigosbasicos_springboot.topics.caching.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Multiple CacheManagers registered
 * */

@EnableCaching
@Configuration
public class CachingConfig {
		//extends CachingConfigurerSupport {   //we could also extend this class, hence it will consider the one from cacheManager method as the Primary
    
	//------------------------------------------- MANAGER PRIMARY
	
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .initialCapacity(50)
                .maximumSize(100)
                //.maximumWeight(maximumWeight)
                //.refreshAfterWrite(duration)
                ;
    }

	@Primary
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();//dynamic cache manager, crea caches a medida que se le pida
        // new CaffeineCacheManager("addresses", "otherCache") //static cache manager, crea caches sólo para esos nombres
        
        caffeineCacheManager.setCaffeine(caffeineConfig());
        caffeineCacheManager.getCache("addresses");//lazy initialization. Creará una cache con ese nombre, usando el dynamic cache manager anterior
        return caffeineCacheManager;
    }
	
	//------------------------------------------- MANAGER ALTERNATIVO
    
	/**
	 * no Caffeine, usa una implementación (propia de SpringBoot) de CacheManager
	 * sólo el usar org.springframework.boot : spring-boot-starter-cache, y la anotación @EnableCaching, ya registra en el contenedor una instancia de CacheManager interface cuya impl es ConcurrentMapCacheManager (propia de SpringBoot)
	 * no hacía falta declarar como Bean quizás, pero lo hacemos para configurar a nuestra manera.
	 * */
    @Bean
    public CacheManager alternateCacheManager() {
    	// Will use this implementation. Not Caffeine or other.
        return new ConcurrentMapCacheManager("addresses", "orderprice");
    }
    
    //------------------------------------------- CACHE RESOLVER (determines manager to use)
    
    /**
     * creamos una clase que implemente CacheResolver, que contenga distintos CacheManager, y la lógica del método resolveCaches determinará cuál usar
     * */
    @Bean
    public CacheResolver cacheResolver() {
        return new MultipleCacheResolver(alternateCacheManager(), cacheManager());
    }
    
    
    public static class MultipleCacheResolver implements CacheResolver {
        
        private final CacheManager simpleCacheManager;
        private final CacheManager caffeineCacheManager;    
         
        public MultipleCacheResolver(CacheManager simpleCacheManager, CacheManager caffeineCacheManager) {
            this.simpleCacheManager = simpleCacheManager;
            this.caffeineCacheManager=caffeineCacheManager;
        }
     
        @Override
        public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
            Collection<Cache> caches = new ArrayList<>();
            if ("getAddress3".equals(context.getMethod().getName())) { // si obtiene el nombre del método, será que tiene que recuperarlo del stacktrace ?? operación costosa en recursos!, o puede saberlo de antemano pq lo llamó con reflexión
                caches.add(caffeineCacheManager.getCache("addresses"));
            } else {
                caches.add(simpleCacheManager.getCache("addresses"));
            }
            return caches;
        }
    }
    
    //-------------------------------------------
    
}
