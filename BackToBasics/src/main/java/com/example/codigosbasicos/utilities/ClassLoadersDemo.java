package com.example.codigosbasicos.utilities;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ClassLoadersDemo {

	public static void main (String[] args) {
		
		ClassLoadersDemo demo = new ClassLoadersDemo();
		demo.start();
		
		//// podemos ver ejemplo en JunitMockitoPowermock project. Se ejecutaba cada clase de test en un classloader distinto.
		
	}
	
	
	private void start() {
		
		// 3 TIPOS DE CLASS LOADERS
				
		// Bootstrap
				//ExtClassLoader
						//AppClassLoader
		
		//Java classes are loaded by an instance of ClassLoader, but main instance and other classes from the JDK, for the JVM, are loaded by the parent one, Bootstrap ClassLoader, which is written in native code
		
		// BOOTSTRAP OR PRIMORDIAL
		//is the parent of all the others. 
		//the bootstrap class loader is written in native code, not Java – so it doesn't show up as a Java class. Due to this reason, the behavior of the bootstrap class loader will differ across JVMs.
				//typically from rt.jar and other core libs in .../jre/bin directory
		
		System.out.println("Classloader of ArrayList:"  + ArrayList.class.getClassLoader());
		
		// EXTENSION
		//load classes that are an extension of the standard core Java classes.
		//from .../lib/ext directory or any other directory mentioned in the java.ext.dirs system property. 
		
		System.out.println("Classloader of Logging:" + Logger.class.getClassLoader());

		// APPLICATION OR SYSTEM
		//loads our own files in the classpath.
				//It loads files found in the classpath environment variable, -classpath or -cp command line option
		
		System.out.println("Classloader of this class:" + this.getClass().getClassLoader());
	    
		//Classloader of ArrayList:null //no se muestra pq el bootstrap class-loader no es clase java sino nativo
		//Classloader of Logging:sun.misc.Launcher$ExtClassLoader@3caeaf62 //se puso el del ejemplo que usaba otra librería
	    //Classloader of this class:sun.misc.Launcher$AppClassLoader@2a139a55
		
		howItWorks();
	}


	private void howItWorks() {
		
		// Inicia en  java.lang.Class.forName()
		// luego java.lang.ClassLoader.loadClass(), este encomienda al parent CL, y va bajando hacia los hijos si no la consigue
		// finalmente la hija llamaría a java.net.URLClassLoader.findClass() method to look for classes in the file system itself.
		
		//Visibility:
			// Las clases cargadas por un child CL pueden ver las clases de los padres, pero no al revés
				// A es cargada en AppCL, B en ExtCL. Las de AppCL pueden ver a A y B, las de ExtCL sólo las suyas.
		
		customClassLoaders();
		
	}


	private void customClassLoaders() {

		/*
		 A few use cases might include:

    	- Helping in modifying the existing bytecode, e.g. weaving agents
    	- Creating classes dynamically suited to the user's needs. e.g in JDBC, switching between different driver implementations is done through dynamic class loading.
    	- Implementing a class versioning mechanism while loading different bytecodes for classes with same names and packages. This can be done either through URL class loader (load jars via URLs) or custom class loaders.

		o Browsers, for instance, use a custom class loader to load executable content from a website. A browser can load applets from different web pages using separate class loaders. 
			The applet viewer which is used to run applets contains a ClassLoader that accesses a website on a remote server instead of looking in the local file system.
			And then loads the raw bytecode files via HTTP, and turns them into classes inside the JVM. Even if these applets have the same name, they are considered as different components if loaded by different class loaders.
		 */
		
		//JUST EXTEND FROM ClassLoader.class, and make your own implementation
		
		// THE TOPIC IS MORE EXTENSIVE THAN IT LOOKS!
		
		contextClassLoaders();
	}


	/**
	In general, context class loaders provide an alternative method to the class-loading delegation scheme introduced in J2SE.

	Like we've learned before, classloaders in a JVM follow a hierarchical model such that every class loader has a single parent with the exception of the bootstrap class loader.
	
	However, sometimes when JVM core classes need to dynamically load classes or resources provided by application developers, we might encounter a problem.
	
	For example, in JNDI the core functionality is implemented by bootstrap classes in rt.jar. But these JNDI classes may load JNDI providers implemented by independent vendors (deployed in the application classpath). This scenario calls for the bootstrap class loader (parent class loader) to load a class visible to application loader (child class loader).
	
	J2SE delegation doesn't work here and to get around this problem, we need to find alternative ways of class loading. And it can be achieved using thread context loaders.
	
	The java.lang.Thread class has a method getContextClassLoader() that returns the ContextClassLoader for the particular thread. The ContextClassLoader is provided by the creator of the thread when loading resources and classes.
	
	If the value isn't set, then it defaults to the class loader context of the parent thread.
	 * */
	private void contextClassLoaders() {
		
		/* Se habla de un problema cuando 
				ie. JNDI carga su core functionality con clases de rt.jar, entonces usará el Bootstrap CL,
				pero es una interfaz que depende de una impl de terceros, y dependerá de clases en librerías del application classpath, las cuales se cargan usando el AppClassLoader,
				y estas clases no son visibles para el Bootstrap CL.
				
					Entonces, Thread class tiene getContextClassLoader(). Este context CL lo provee el otro thread que creó al actual y parece que permite acceder a ese CL.
		*/
	}
	
	
	
}
