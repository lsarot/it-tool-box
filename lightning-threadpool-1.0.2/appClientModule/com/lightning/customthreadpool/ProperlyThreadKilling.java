package com.lightning.customthreadpool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class ProperlyThreadKilling {

	
	public void properlyKillThread() {
		
		//CREAR
		MyStopableThread thread = new MyStopableThread(null);
		
        
        //*** INTERRUMPIR (flag approach)
        
        System.out.println("\nStarting thread: " + thread);
        thread.start();
        System.out.println("Background process successfully started.");
        
        long ns = System.nanoTime();
		try {
			System.out.println("Stopping thread: " + thread);
			if (thread != null) {
				thread.terminate();//sets flag
				thread.join();
				System.out.println("Thread successfully stopped.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Elapsed time to stop thread in ms: " + (System.nanoTime()-ns)/1000000d);
		
		//*** INTERRUMPIR (interrupt approach)
		
		thread = new MyStopableThread(null);
		System.out.println("\nStarting thread: " + thread);
        thread.start();
        System.out.println("Background process successfully started.");
        
        ns = System.nanoTime();
		try {
			//thread.destroy(); deja bloqueados recursos del sistema
			thread.interrupt();//you can immediately break out of interruptable calls, which you can't do with the flag approach.
			thread.join();
			System.out.println("Thread successfully stopped.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Elapsed time to stop thread in ms: " + (System.nanoTime()-ns)/1000000d);
		
		//puedo colocar esto en sitios estratégicos del flujo del thread para liberar y parar
		//if (Thread.currentThread().isInterrupted()) {
			  // cleanup and stop execution.. for example a break in a loop
		//}
		
		
		//*** INTERRUMPIR (count down latch)
		
		final CountDownLatch countdownLatch = new CountDownLatch(1);
		thread = new MyStopableThread(countdownLatch);
		System.out.println("\nStarting thread: " + thread);
        thread.start();
        System.out.println("Background process successfully started.");
		
        ns = System.nanoTime();
        try {
        	if (countdownLatch != null) {countdownLatch.countDown();} 
        	if (thread != null) {
        		thread.join(); 
        	}
        	System.out.println("Thread successfully stopped.");
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
        System.out.println("Elapsed time to stop thread in ms: " + (System.nanoTime()-ns)/1000000d);
        
        
        System.runFinalization();
        
	}
	
	
	
	public class MyStopableThread extends Thread {
		
		//con flag
	    private volatile boolean running = true;//volatile pq será accedida por este mismo thread y el que la modifica
	    public void terminate() { running = false; }

	    //con CountDownLatch
	    private final CountDownLatch countdownlatch;
	    public MyStopableThread(CountDownLatch countDownLatch) {
	    	this.countdownlatch = countDownLatch;
	    }
	    
	    //interrumpt sólo se evalúa en el while
	    
	    @Override
	    public void run() {
	    	try {
	    		int processingTime = 6000;//ms
	    	
		    	//EJEMPLO CON FLAG O INTERRUPT
		    	if (countdownlatch == null) {
		 	    	while (running && !Thread.currentThread().isInterrupted()) {
		 	    		System.out.println("processing...");
		            	//este sleep representa ejecución de la tarea. No es el habitual de pocos ms para no bloquear el procesador!
		 	    		for (int i = 0; (i < (processingTime/50)) && this.running; i++) { Thread.sleep(50); }
			        }
		    	}
		    	else //EJEMPLO CON COUNTDOWNLATCH
		    	{
		    		System.out.println("processing...");
		    		while (!countdownlatch.await(processingTime, TimeUnit.MILLISECONDS)) {
		    			System.out.println(".");
		    		}
		    	}
	    	} catch (InterruptedException e) {
            	e.printStackTrace();
                running = false;
            }
	    }
	}
	

}
