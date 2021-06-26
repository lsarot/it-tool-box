package com.lightning.customthreadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*PARA UTILIZAR FUTURO EN MI POOL (mucho cambio)
 * Debo crear una impl de Future y devolverla, guardarla en una cola. Esta guarda Objeto resultado, isDone, un callable, threadAsociado, etc.
 * Usando el futuro puedo recuperar el resultado, cancelar el thread, entre otros.
 * Si lo cancelamos, y está en la queue, se elimina de la queue el que apunte a la misma ref.. Si es forzada y el thread asociado existe, se interrumple el thread.
 * Se van guardando en otra lista de Done los futuros terminados.
 * Y muchos otros cambios.
 * Se puede usar las interfaces:
I ExecutorService submit, execute
I CompletionService submit, poll
I Callable call
I Future isCancelled, isDone, get, cancel 
 */

/**
 * ***** SE CONSIDERA:
 * MIN Y MAX POOL THREADS
 * AUMENTAR POOL SI TOMA MÁS DE X TIEMPO PARA OBTENER UN THREAD
 * REDUCIR POOL PASADO UN LAPSO DE TIEMPO SIN QUE SE PIDA REALIZAR TAREAS
 * REALIZAR TAREAS SINCRONAS Y ASINCRONAS
 * DETENER EL POOL. NO PERMITE PROGRAMAR MÁS TAREAS. (Su llamado no bloquea!)
 * 		Cuando se llama a detener, pueden ocurrir varios escenarios:
 * 		.si es con force=false, se ejecutan todas las submitted tasks antes de terminar el scheduler thread.
 * 		.si es con force=true, se vacía lista scheduled tasks y pending tasks y termina el scheduler... (las scheduled pasan a pending y las síncronas directo a pending, es pending de pendiente por asignar un thread básicamente!)
 * 		.con force=true, pueden haber tareas que ya están asignadas a un thread, ejecutándose, esas no se detienen!
 * LIBERAR THREADS DEL POOL LUEGO DE DETENERLO
 * ESPERAR POR TODAS LAS TAREAS QUE TERMINEN. Con pool.waitForAll, se espera por las asignadas a threads.
 * 		.se asignan a threads todas si terminate es con force=false
 * 
 * 
 * ***** NO CONSIDERA:
 * TRABAJAR CON MIN/MAX IDLE THREADS, están los contadores, se modifican donde debe ser, pero no se consideran al destruir o crear nuevos threads!
 * EVALUAR TODAS LAS VARIABLES EN EL .build DEL THREAD POOL BUILDER
 * */

public class LightningThreadPoolBuilder {
	
	private LightningThreadPoolBuilder() {}
	
	public static ThreadPoolBuilder createCustomPool() {
		LightningThreadPoolBuilder builder = new LightningThreadPoolBuilder();
		return builder.new ThreadPoolBuilder();
		//return new ThreadPoolBuilder(); //si hago esto, declaro static las inner class
	}
	
	
	//---------------------------------------------------------------------
	/**
	 * THE ACTUAL THREAD POOL BUILDER, DESIGNED USING BUILD PATTERN
	 * */
	public class ThreadPoolBuilder {

		private static final int MIN_POOL_SIZE = 1;
		private static final int MAX_POOL_SIZE = 100;
		private final int machineLogicalProcs;

		{
			machineLogicalProcs = Runtime.getRuntime().availableProcessors();
			maxPoolSize = machineLogicalProcs;
		}
		
		private ThreadPoolBuilder() {}//siendo inner class, puede ser private, aún public no se puede instanciar desde afuera pq la externa tiene constructor private y se necesita una instancia!

		//for user config
		private int minPoolSize = MIN_POOL_SIZE;
		private int maxPoolSize;
		private int minIdleThreads = 1;//para futuro, inicia threads nuevos si hay menos de ese número ociosos
		private int maxIdleThreads = 1;//para futuro, termina threads que estén ociosos hasta alcanzar ese máximo
		
		public ThreadPoolBuilder maxPoolSize(int i) {
			this.maxPoolSize = i;
			return this;
		}
		public ThreadPoolBuilder minPoolSize(int i) {
			this.minPoolSize = i;
			return this;
		}
		
		public LightningThreadPool build() {
			//if everything ok, returns the instance!
			
			if (maxPoolSize > machineLogicalProcs) {//poniendo el max a cantidad de available processors, no hay mejora en los tiempos!
				//maxPoolSize = machineLogicalProcs;
				//System.err.println("Max pool size setted to max available logical processors in your machine: " + machineLogicalProcs);
			}
			
			if (this.maxPoolSize < MIN_POOL_SIZE || this.maxPoolSize > this.MAX_POOL_SIZE) {
				throw new RuntimeException("max pool size must be between 1 and 10.");
			}
			if (this.minPoolSize < MIN_POOL_SIZE || this.minPoolSize > this.MAX_POOL_SIZE) {
				throw new RuntimeException("min pool size must be between 1 and 10.");
			}
			if (this.minPoolSize > this.maxPoolSize)
				minPoolSize = maxPoolSize;//bajamos el min hasta el max y no al revés (sin mayor razonamiento)(pero el mínimo debería mantenerse bajo para no consumir memoria)
			
			//sólo por salir del paso, debería chequearse junto a minPoolSize y maxPoolSize y pueden haber N variables involucradas
			if (maxIdleThreads < minIdleThreads) {
				maxIdleThreads = minIdleThreads;
			}
			
			return new LightningThreadPool(minPoolSize, maxPoolSize);
		}
	}
	
	
	//---------------------------------------------------------------------
	/**
	 * THE LIGHTNING THREAD POOL IMPL
	 * */
	public class LightningThreadPool {
		
		private LightningThreadPool(int minPoolSize, int maxPoolSize) {
			this.minPoolSize = minPoolSize;
			this.maxPoolSize = maxPoolSize;
			
			initializePool();
			initializeScheduler();
		}

		private AtomicInteger totalTaskCount = new AtomicInteger(0);
		private AtomicInteger totalAssignedCount = new AtomicInteger(0);
		private AtomicInteger totalDoneCount = new AtomicInteger(0);
		private AtomicInteger totalFailedCount = new AtomicInteger(0);
		private MyScheduler scheduler;
		private List<RecyclableThread> threadPool = new ArrayList<>();
		private volatile List<Runnable> pendingRunnables = new ArrayList<>();
		private volatile boolean terminated = false;//flag que evita que se puedan enviar más tareas síncronas o asíncronas!
		private final Object signalToTerminate = new Object();
		private volatile boolean allTasksAssignedAreDone = false;
		
		private int minPoolSize = 1;
		private int maxPoolSize = 1;
		
		private int minIdleThreads = 1;
		private int maxIdleThreads = 1;
		
		private AtomicInteger currentPoolSize = new AtomicInteger(0);//para uso futuro
		private AtomicInteger currentIdleThreads = new AtomicInteger(0);

		private long lastAddedTaskTime;//momento en que añade la última tarea, para determinar si se debe eliminar threads porque ha pasado tanto tiempo
		
		private static final int waitingMillisThreshold = 400;//max time to wait before considering to create a new thread
		
		private void initializePool() {
			for (int i = 0; i < minPoolSize; i++) {
				addOneThread();
			}
		}
		
		private void initializeScheduler() {
			this.scheduler = new MyScheduler();
			this.scheduler.start();
		}
		
		//no se considera aún idle threads
		private void addOneThread() {
			synchronized (threadPool) {
				if (currentPoolSize.get() < maxPoolSize) {
					RecyclableThread rt = new RecyclableThread();
					rt.start();
					this.threadPool.add(rt);
					this.currentPoolSize.incrementAndGet();
					idleCountIncrement();
					System.out.println("+ " + currentPoolSize.get());
				}
			}
		}
		
		//no se considera aún idle threads
		private void removeOneThread() {
			synchronized (threadPool) {
				if (currentPoolSize.get() > minPoolSize) {
					boolean removed = false;
					while (!removed) {
						for (RecyclableThread rt : threadPool) {
							if (rt.runnable == null) {//si no está asignado un ejecutable
								rt.running = false;
								threadPool.remove(rt);
								this.currentPoolSize.decrementAndGet();
								idleCountDecrement();
								removed = true;
								System.out.println("- " + currentPoolSize.get());
								break;
							}
						}
					}
				}
			}
		}
		
		private void removeAllThreadsFromPool() {
			for (RecyclableThread rt : threadPool) {
				rt.running = false;
			}
			threadPool.clear();
		}
		
		public void terminatePool(boolean force) {
			this.terminated = true;
			this.scheduler.shutDownScheduler(force);
		}
		
		//to check whether the pool is terminated by the user
		public boolean isPoolTerminated() {
			return this.terminated;
		}
		
		/**
		 * Returns True if total done are less than total assigned to threads, False otherwise.
		 * Consider that if you forcedly terminate the pool, not all submitted tasks will be assigned to threads,
		 * so this only considers those that would be done by waiting (pool.waitForAll method)
		 * */
		public boolean poolHasPendingWork() {
			return (totalDoneCount.get() < totalAssignedCount.get());
		}
		
		/**
		 * Waits for all just to be assigned to a thread, no to finish!
		 * */
		public void waitForAll() {
			try {
				synchronized (signalToTerminate) {
			         while (!allTasksAssignedAreDone) {
			        	 signalToTerminate.wait();
			         }
			     }
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		private void idleCountIncrement() {
			this.currentIdleThreads.incrementAndGet();
			//puedo evaluar si maxIdleCount es menor a current y terminar un thread
			//para este caso de liberar, debe hacerse considerando que haya pasado un tiempo sin trabajar el pool
				//no estar creando y terminando a cada milisegundo
		}
		
		private void idleCountDecrement() {
			this.currentIdleThreads.decrementAndGet();
			//puedo evaluar si minIdleCount es mayor a current y crear un thread
		}
		
		/**
		 * Realmente asigna a un thread y continúa, no espera!
		 * */
		public void executeAndWait(Runnable r) throws Exception {
			if (terminated)
				throw new Exception("Can't add new task once pool has been terminated.");
				
			totalTaskCount.incrementAndGet();
			executeAndWaitInternal(r);
		}
		
		private Object exeAndWait = new Object();
		
		//aquí acceden el main-thread(quien tiene el pool reference) y el scheduler solamente
		//2 threads a la vez max, intentando acceder al pool
		private void executeAndWaitInternal(Runnable r) {
			//se usa pendingRunnables list como un flag para saber si hay operaciones pendientes
			//ahí se agregan las nuevas (síncronas), y las scheduled que nos va enviando, borrándose de su lista y añadiéndose a esta
			//el orden lo gestiona el bloque synchronized
			//pero pudiera tomarse las tareas de dicha lista. E incluso insertar bajo un criterio de prioridad dado por el usuario!
			this.pendingRunnables.add(r);
			this.lastAddedTaskTime = System.currentTimeMillis();
			long msToObtainThread = lastAddedTaskTime;
			boolean assigned = false;
			
			while (!assigned) {
				synchronized (threadPool) {
					//en este punto pudiera haberse forzado a terminar el pool (es para llamados del scheduler básicamente, async)
					if (this.terminated && this.pendingRunnables.size() == 0) {
						System.out.println("returning..");
						return;
					}
				
					for (RecyclableThread rt : threadPool) {
						if (rt.runnable == null) {
							msToObtainThread = System.currentTimeMillis() - msToObtainThread;
							totalAssignedCount.incrementAndGet();
							idleCountDecrement();
							rt.setRunnable(r);
							assigned = true;
							this.pendingRunnables.remove(r);
							break;
						}
					}
				}
			}
			
			
			//if it last more than a threshold to obtain a thread AND is is ok to create a new one, then creates it
			if (msToObtainThread > waitingMillisThreshold && currentPoolSize.get() < maxPoolSize)
				addOneThread();
			
		}
		
		public void executeAsync(Runnable r) throws Exception {
			if (terminated)
				throw new Exception("Can't add new task once pool has been terminated.");
			
			totalTaskCount.incrementAndGet();
			this.scheduler.scheduled.add(r);
		}
		
		
		//---------------------------------------------------------------------
		/**
		 * TASKS EXECUTION SCHEDULER
		 * */
		private class MyScheduler extends Thread {
			
			private MyScheduler() {}

			private volatile boolean running = true;
			private List<Runnable> scheduled = new ArrayList<>();
			private static final int elapsed_idle_time_to_shrink_pool = 60000;//ms
			
			@Override
			public void run() {
				int sleepingTime = 50;//ms
				try {
					//mientras este funcionando, hayan agendados o pendientes a la espera de un thread
					while (running || scheduled.size() > 0 || pendingRunnables.size() > 0) {
						if (scheduled.size() > 0) {
							Runnable r = scheduled.get(0);
							scheduled.remove(0);
							executeAndWaitInternal(r);
						}
						
						determinePoolShrinkingNeeded();
						
						//con esto puedo parar el sleep incluso con un flag
						for (int i = 0; (i < (sleepingTime/50)) && running; i++) { Thread.sleep(50); }
					} 
					
					//al terminar el thread del scheduler, termino el thread pool
					shutDownSchedulerAndPool();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			//no se considera idle threads
			//ni en método creación ni en destrucción
			private void determinePoolShrinkingNeeded() {
				if ((System.currentTimeMillis() - lastAddedTaskTime) > elapsed_idle_time_to_shrink_pool
						&& scheduled.size() == 0 && pendingRunnables.size() == 0
						&& currentPoolSize.get() > minPoolSize) {
						removeOneThread();
					}
			}
			
			private void shutDownScheduler(boolean force) {
				running = false;
				if (force) {
					scheduled.clear();
					pendingRunnables.clear();
				}
			}
			
			private void shutDownSchedulerAndPool() {
				scheduled.clear();//shouldn't be necessary!
				pendingRunnables.clear();//shouldn't be necessary!
				System.err.println();
				System.err.println("THREAD POOL ENDS UP WITH " + threadPool.size() + " THREADS");
				
				while (totalDoneCount.get() < totalAssignedCount.get()) {}//espera que terminen las que estaban asignadas al menos!
				
				removeAllThreadsFromPool();
				System.err.println("THREAD POOL HAS NOW " + threadPool.size() + " THREADS (must be 0)");
				System.gc();
				System.err.println("TOTAL REQUESTED TASKS: " + totalTaskCount.get() + " | TOTAL ASSIGNED TO THREADS: " + totalAssignedCount.get() + " | \n"
											+ "TOTAL DONE TASKS: " + totalDoneCount.get() + " | TOTAL FAILED TASKS: " + totalFailedCount.get());
				
				synchronized (signalToTerminate) {
			         allTasksAssignedAreDone = true;
			         signalToTerminate.notifyAll();
			     }
			}
			
		}
		
		
		//---------------------------------------------------------------------
		/**
		 * THREAD IMPL THAT ALLOWS TO BE REUSED TO EXECUTE RUNNABLES
		 * */
		private class RecyclableThread extends Thread {
			
			private RecyclableThread() {}

			private volatile boolean running = true;
			private Runnable runnable;
			
			public void setRunnable(Runnable runn) {this.runnable = runn;}
			
			@Override
			public void run() {
				try {
					do {
						if (runnable != null) {
							try {
								this.runnable.run();
							} catch (Exception e) {//falla la tarea
								System.err.println("Task execution failed! : " + e.getMessage());
								totalFailedCount.incrementAndGet();
							} finally {
								runnable = null;//habilito de nuevo!
								totalDoneCount.incrementAndGet();
								idleCountIncrement();
							}
						}
						
						Thread.sleep(50);
					} while (running);
				} catch (Exception e) {//falla el thread
					System.err.println("Task execution failed! : " + e.getMessage());
					totalFailedCount.incrementAndGet();
					totalDoneCount.incrementAndGet();//para que se considere cuando tenga que salir y esperar que termine todo!
					//idleCountIncrement();//no cambia el nro de idle threads pq este thread murió!
					threadPool.remove(this);
					currentPoolSize.decrementAndGet();
					System.out.println("- " + currentPoolSize.get() + " (failed thread)");
				}
			}
			
		}
		
	}
	
	
}
