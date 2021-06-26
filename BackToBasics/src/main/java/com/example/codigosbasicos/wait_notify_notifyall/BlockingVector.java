package com.example.codigosbasicos.wait_notify_notifyall;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


/**
 * https://www.baeldung.com/java-wait-notify
 * No es de este tutorial, pero se ve bueno!
 * */

// The first thing you have to do is to identify the conditions that you want the methods to wait for. In this case, you will want the put() method to block
// until there is free space in the store, and you will want the take() method to block until there is some element to return.
public class BlockingVector {
    
    private final int[] vector;
    private int acumulado;
    
    public BlockingVector(int capacity) {
        vector = new int[capacity];
        acumulado = 0;
    }
    
    /* Firstly, you need to ensure that any calls to wait() or notify() are within a synchronized region of code (with the wait() and notify() calls
     * being synchronized on the same object). The reason for this (other than the standard thread safety concerns) is due to something known as a missed signal. 
     * > Si no se usa un SYNCHRONIZED pueden suceder escenarios como estos:
     *     - Supóngase que la capacidad es 10 y actualmente está lleno el vector, un T1 que usando el put() puede ejecutarse hasta justo antes del wait() habiendo
     *       chequeado que el vector está full, inmediatamente un T2 usando take() recoje un item y llama a notify() (el vector ya tiene 9 elementos) y, T1 debiera
     *       poder introducir su item, pero lo que hace es llamar al wait() quedando en espera cuando no debiera.
     *     - Otro caso sería i.e. supóngase el vector con 9 de 10 items actualmente, un T1 llama a put() y llega a ejecutarse hasta la instrucción que añade el 
     *       elemento nro 10, otro T2 llama a put() y encuentra que debe esperar y llama a wait(), luego T1 llama a notify() y por estar synchronized sobre el mismo
     *       objeto despierta a T2 el cual intentará añadir un 11er item cuando no debiera.
     */
    public synchronized void put(int i) throws InterruptedException {
        System.out.println("Método put con " + i);
        /* Secondly, you need to put the condition you are checking in a while loop, rather than an if statement, due to a problem known as spurious wake-ups.
         * This is where a waiting thread can sometimes be re-activated without notify() being called. Putting this check in a while loop will ensure that if a 
         * spurious wake-up occurs, the condition will be re-checked, and the thread will call wait() again.
         */
        while(acumulado == vector.length) {
            System.out.println("     Justo antes del wait para añadir " + i);
            wait();
            System.out.println("     Justo después del wait para añadir " + i);
            // Aunque un bloque synchronized impida que 2 threads accedan al mismo tiempo a este objeto, con el llamado a wait el cual pone en espera este hilo,
            // se libera el bloqueo y pueden acceder otros threads al objeto, en este ejemplo que es un wait sobre una instancia de esta clase el objeto sería 
            // esta instancia.
        }
        
        System.out.print("   Introduciendo " + i + " en el vector.  ");
        vector[acumulado] = i;
        acumulado++;
        System.out.println(toString());
        notify();
        /* Cuando hago notify o notifyAll, se despierta(n) thread(s), pero no prosiguen hasta que el thread que hizo el notify libere el bloqueo, en este caso culminando el método.
         * Este notify del método put incluso despierta threads que estaban dormidos también en este mismo método put, pero ellos no son capaces de introducir más
         * .. items de lo permitido en el vector ya que el loop while siempre hará el chequeo de espacios disponibles.
         */
    }

    public synchronized int take() throws InterruptedException {
        System.out.println("Método take");
        while(acumulado == 0) {
            System.out.println("     Justo antes del wait para tomar");
            wait();
            System.out.println("     Justo después del wait para tomar");
        }

        int i = vector[acumulado-1];
        vector[acumulado-1] = 0;
        acumulado--;
        notify();// NotifyAll() despierta a todos los que hicieron wait sobre el mismo objeto, en este caso el objeto es la misma instancia de este thread (this en otras palabras)
        return i;
    }
    
    @Override
    public String toString() {
        String st = "";
        for(int i=0;i<vector.length;i++) { st += "[" + vector[i] + "]";}
        return st;
    }
//---------------------------------------------------------------------------------------------------------------------------------------------------
// *** Java 1.5 introduced a new concurrency library (in the java.util.concurrent package) which was designed to provide a higher level abstraction over the wait/notify mechanism.
//	Using these new features, you could rewrite the original example like so: ***

    private Lock reeLock = new java.util.concurrent.locks.ReentrantLock();// TIPO DE BLOQUEO SIMILAR AL DE BLOQUES SYNCHRONIZED
    private Condition notFull = reeLock.newCondition();// SE CREAN CONDITIONS SOBRE LAS CUALES ESPERAR O NOTIFICAR
    private Condition notEmpty = reeLock.newCondition();
    
    public void Put(int i) throws InterruptedException {
        reeLock.lock();
        try {
            
            while(acumulado == vector.length) { 
                notFull.await();
            }
            vector[acumulado] = i;
            acumulado++;
            notEmpty.signal();
            
        } finally {
            reeLock.unlock();
        }
    }
    
    public int Take() throws InterruptedException {
        reeLock.lock();
        int i = 0;
        try {
            
            while(acumulado == 0) {
                notEmpty.await();
            }
            i = vector[acumulado-1];
            vector[acumulado-1] = 0;
            acumulado--;
            notFull.signal();
            
        } finally {
            reeLock.unlock();
        }
        return i;
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------
    // *** En el ejemplo de esta clase se utiliza la misma instancia para hacer los bloqueos, pero vamos a demostrar cómo hacer el bloqueo sobre la lista en sí ***
    
    // Para que un hilo se bloquee basta con que llame al método wait() de cualquier objeto. Sin embargo, es necesario que dicho hilo haya marcado ese objeto
    //..como ocupado por medio de un synchronized. Si no se hace así, saltará una excepción de que "el hilo no es propietario del monitor"
    // Una vez que nos metemos en el wait(), el objeto bloqueado queda marcado como "desocupado", de forma que otros hilos pueden usarlo. Cuando despertemos y salgamos del wait(), volverá a marcarse como "ocupado".
    public void put2(int i) throws InterruptedException {
        synchronized(vector) {
            
            while(acumulado == vector.length) {
                vector.wait();
            }
            vector[acumulado] = i;
            acumulado++;
            vector.notify();
            
        }
    }
    
    public int take2() throws InterruptedException {
        synchronized(vector) {
        
            while(acumulado == 0) {
                vector.wait();
            }
            int i = vector[acumulado-1];
            vector[acumulado-1] = 0;
            acumulado--;
            vector.notify();
            return i;
        
        }
    }
}

// ALGUNAS CONSIDERACIONES:
/*
  1. >>> What happens when notify() is called and no thread is waiting?

In general practice, this will not be the case in most scenarios if these methods are used correctly. Though if the notify() method is called when no other thread is waiting, notify() simply returns and the notification is lost.

Since the wait-and-notify mechanism does not know the condition about which it is sending notification, it assumes that a notification goes unheard if no thread is waiting. A thread that later executes the wait() method has to wait for another notification to occur.

  2. >>> Can there be a race condition during the period that the wait() method releases OR reacquires the lock?

The wait() method is tightly integrated with the lock mechanism. The object lock is not actually freed until the waiting thread is already in a state in which it can receive notifications. It means only when thread state is changed such that it is able to receive notifications, lock is held. The system prevents any race conditions from occurring in this mechanism.

Similarly, system ensures that lock should be held by object completely before moving the thread out of waiting state.

  3. >>> If a thread receives a notification, is it guaranteed that the condition is set correctly?

Simply, no. Prior to calling the wait() method, a thread should always test the condition while holding the synchronization lock. Upon returning from the wait() method, the thread should always retest the condition to determine if it should wait again. This is because another thread can also test the condition and determine that a wait is not necessary — processing the valid data that was set by the notification thread.

This is a common case when multiple threads are involved in the notifications. More particularly, the threads that are processing the data can be thought of as consumers; they consume the data produced by other threads. There is no guarantee that when a consumer receives a notification that it has not been processed by another consumer. As such, when a consumer wakes up, it cannot assume that the state it was waiting for is still valid. It may have been valid in the past, but the state may have been changed after the notify() method was called and before the consumer thread woke up. Waiting threads must provide the option to check the state and to return back to a waiting state in case the notification has already been handled. This is why we always put calls to the wait() method in a loop.

  4. >>> What happens when more than one thread is waiting for notification? Which threads actually get the notification when the notify() method is called?

It depends on many factors.Java specification doesn’t define which thread gets notified. In runtime, which thread actually receives the notification varies based on several factors, including the implementation of the Java virtual machine and scheduling and timing issues during the execution of the program. There is no way to determine, even on a single processor platform, which of multiple threads receives the notification.

Just like the notify() method, the notifyAll() method does not allow us to decide which thread gets the notification: they all get notified. When all the threads receive the notification, it is possible to work out a mechanism for the threads to choose among themselves which thread should continue and which thread(s) should call the wait() method again.

  5. >>> Does the notifyAll() method really wake up all the threads?

Yes and no. All of the waiting threads wake up, but they still have to reacquire the object lock. So the threads do not run in parallel: they must each wait for the object lock to be freed. Thus, only one thread can run at a time, and only after the thread that called the notifyAll() method releases its lock.

  6. >>> Why would you want to wake up all of the threads if only one is going to execute at all?

There are a few reasons. For example, there might be more than one condition to wait for. Since we cannot control which thread gets the notification, it is entirely possible that a notification wakes up a thread that is waiting for an entirely different condition. By waking up all the threads, we can design the program so that the threads decide among themselves which thread should execute next. Another option could be when producers generate data that can satisfy more than one consumer. Since it may be difficult to determine how many consumers can be satisfied with the notification, an option is to notify them all, allowing the consumers to sort it out among themselves.

*/




