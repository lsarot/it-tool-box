package com.example.codigosbasicos_springboot.topics.caching.caffeine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
//@CacheConfig(cacheNames={"addresses"})   //configures beforehand some common values to all other annotations in the class
public class AddressService {
 
    @Autowired
    CacheManager cacheManager;//va a inyectar la   @Primary
    
    
    //@Autowired
    //@Qualifier("alternateCacheManager")
    //CacheManager alternateCacheManager;
 
    
    /**
     * el framework sólo sabe que el método recibe un long y devuelve un String.
     * Usará el CacheResolver para determinar el CacheManager a usar ??, o usará el CacheManager de la clase ?, ya que abajo un ejemplo define cacheResolver a usar
     * tomará la cache con nombre addresses y buscará por customerId como key,
     * si encuentra (en alguna de las cachés) devuelve su valor, sino encuentra, ejecuta el método y guarda en caché/s su valor.
     * */
    //@Cacheable(value = "addresses", key = "customerId")
    @Cacheable(cacheNames = {"addresses"}) // cacheManager = "alternateCacheManager" to specify it
    public String getAddress(long customerId)  {
        log.info("Method getAddress is invoked for customer {}", customerId);
        return "123 Main St " + customerId;
    }

    
    /** Definimos un CacheResolver
     * para usar un cache manager según una lógica implementada por nosotros!
     * */
    @Cacheable(cacheNames = "addresses", cacheResolver = "cacheResolver")
    public String getAddress3(long customerId)  {
        log.info("Method getAddress3 is invoked for customer {}", customerId);
        return "123 Main St " + customerId;
    }
    
    
    /** Uso sin @Cacheable
     * debemos hacer get y set sobre la cache!
     * */
    public String getAddress2(long customerId) {
    	Cache cache = cacheManager.getCache("addresses2");
    	ValueWrapper value = cache.get(customerId);
    	if (value != null)
    		return value.get().toString();

        log.info("Method getAddress2 is invoked for customer {}", customerId);
        String address = "123 Main St " + customerId;
        cache.put(customerId, address);
        
        //pasando un loader
        cacheManager.getCache("").get("1", ()->{
        	return null;
        });
        
        return address;
    }
    

    /** TO TEST OTHER ANNOTATIONS
     * CachePut always executes the method, but it saves it's return value for usage on another part of our app (based on conditions condition and unless)
     * 		this behavior can be set on normal configuration (without Springboot) with help of a custom expiration policy, we decide whether to set expiration to 0.
     * CacheEvict removes from cache, note we specified other cacheManager!... that annotation should be used on a method with eviction purpose!
     * */
    //@CachePut(value="addresses")   //   The difference between @Cacheable and @CachePut is that @Cacheable will skip running the method, whereas @CachePut will actually run the method and then put its results in the cache.
    @CachePut(value="addresses", condition="#customerId==1", unless="#result.length()<10") // |  condition="#customer.name=='Tom'"  |   unless="#result.length()<64"     // WILL CACHE ONLY IF CONDITION APPLIES, but will always execute method!
    //@CacheEvict(value="addresses", allEntries=true, cacheManager="alternateCacheManager")   //will clean cache when invoked!... we should use it on a dedicated method!
		    /* TO DECLARE multiple annotations
		    @Caching(evict = { 
		    		  @CacheEvict("addresses"), 
		    		  @CacheEvict(value="directory", key="#customer.name") }) */
    public String getAddress4(long customerId)  {
        log.info("Method getAddress4 is invoked for customer {}", customerId);
        return "123 Main St " + customerId;
    }
    

}
