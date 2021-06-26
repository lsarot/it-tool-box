package com.example.codigosbasicos.sincronizacion;


public class SyncDemo {
    
    public synchronized void test(String name) {
        for(int i=0;i<10;i++) {
            System.out.print("(" + name + " :: " + i + ")");
        }
    }
}
/* > Note: synchronized blocks the next thread's call to method test() as long as the previous thread's execution is not finished. Threads can access this method one at
 * a time. Without synchronized all threads can access this method simultaneously.
 * > When a thread calls the synchronized method 'test' of the object (here object is an instance of 'SyncDemo' class) it acquires the lock of that object, any new thread 
 * cannot call ANY synchronized method of the same object as long as previous thread which had acquired the lock does not release the lock.
 * > Similar thing happens when any static synchronized method of the class is called. The thread acquires the lock associated with the class(in this case any non static 
 * synchronized method of an instance of that class can be called by any thread because that object level lock is still available). Any other thread will not be able 
 * to call any static synchronized method of the class as long as the class level lock is not released by the thread which currently holds the lock.
 *
 * Más detallado:
 * > Synchronized keyword in Java has to do with thread-safety, that is, when multiple threads read or write the same variable. This can happen directly (by accessing the same variable) or indirectly (by using a class that uses another class that accesses the same variable).
 * The synchronized keyword is used to define a block of code where multiple threads can access the same variable in a safe way.
 * > Syntax-wise the synchronized keyword takes an Object as it's parameter (called a lock object), which is then followed by a { block of code }.
 *    - When execution encounters this keyword, the current thread tries to "lock/acquire/own" (take your pick) the lock object and execute the associated block of code after the lock has been acquired.
 *    - Any writes to variables inside the synchronized code block are guaranteed to be visible to every other thread that similarly executes code inside a synchronized code block using the same lock object.
 *    - Only one thread at a time can hold the lock, during which time all other threads trying to acquire the same lock object will wait (pause their execution). The lock will be released when execution exits the synchronized code block.
 * > Adding synchronized keyword to a method definition is equal to the entire method body being wrapped in a synchronized code block with the lock object being this (for instance methods) and ClassInQuestion.getClass() (for class methods).
 * > NOTE: It is not enough to complete a write operation in a thread before (wall-clock time) another thread reads it, because hardware could have cached the value of the variable, and the reading thread would see the cached value instead of what was written to it.
 * > Thus in Java's case, you have to follow the Java Memory Model to ensure that threading errors do not happen.
 *   In other words: Use synchronization, atomic operations or classes that use them for you under the hoods.  
 * 
 * CONCLUSIÓN: 
 * SI EL MÉTODO ES NON-STATIC(de instancia) SYNCHRONIZED el bloqueo ocurre sobre los métodos non-static synchronized DE DICHA INSTANCIA.
 * SI EL MÉTODO ES STATIC(de clase) SYNCHRONIZED el bloqueo ocurre sobre los métodos static synchronized DE LA CLASE.
 * EL BLOQUEO SE PUEDE SEÑALAR COMO ETIQUETA DEL MÉTODO O EN UN BLOQUE DE CÓDIGO
 *      > en el método aplica para todo el código del método y bloquea sobre instancia o clase según si es método de instancia o de clase.
 *      > en un bloque synchronized(objeto){...} aplica sobre ese bloque de código, y para hacer el bloqueo sobre instancia o clase debo usar this o miClase.class
 *           como parámetro respectivamente, esto mejora los tiempos ya que no bloqueas todo el método sino especificamente lo que requiere atomicidad.
 *      > si quiero mejorar la eficienciencia puedo hacer el bloqueo sobre un objeto cualquiera, por ej. supóngase una clase que tiene los métodos read y write 
 *           sobre una cola de mensajes, no quiero que los escritores se solapen, pero quiero que los lectores puedan leer al mismo tiempo que se llena la lista.
 *           Creo un (Object writeLock = new Object();) y hago el bloqueo especificamente sobre dicho objeto con synchronized(writeLock) {...}         
 */