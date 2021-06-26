package com.example.junitmockitopowermock.customrunners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerScheduler;

import com.example.junitmockitopowermock.paralleltestsexecution.ParallelTestsExecutor2;


/**
 * RUNNER DE OTRA PERSONA
 * 
 * MUESTRA BIEN LOS RESULTADOS DETALLADOS POR MÉTODO,
 * no puedo recuperar Category, pero puedo setear las clases a ejecutar según categoría y hacer uno o varios que usen este runner.
 * 
 * */
public class ParallelSuite extends Suite {

	
    public ParallelSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        super(klass, builder);

        setScheduler(new RunnerScheduler() {

        	//POOL JAVA
            private final ExecutorService service = Executors.newFixedThreadPool(4);
            
            // POOL PERSONAL
            /*private final LightningThreadPool pool = LightningThreadPoolBuilder
    				.createCustomPool()
    				.minPoolSize(3)
    				.maxPoolSize(10)
    				.build();
			*/

            /**
             * Aquí llega un llamado por cada clase a testear, pero no es la clase en sí, no podemos ver sus Annotations.
             * */
            public void schedule(Runnable childStatement) {
                service.submit(childStatement);
            	//try { pool.executeAsync(childStatement); } catch (Exception e) {e.printStackTrace();}
            }

            public void finished() {
                try {
                    service.shutdown();
                    service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                    
                    //pool.terminatePool(false);
                    //pool.waitForAll();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        });
        
    }

}
