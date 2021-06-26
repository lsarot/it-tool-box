package com.JNA.leo.jnaexample;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;

/**
 * 			JNA DEMO
 * */

public class JNAdemo {

	/**
	 * In order to test JNA, we actually don’t need any external shared library.
	 * JNA itself provides its own library called “c” that gives access to a lot of the C functionality.
	 * */
	
	public static void main(String[] args) {
    
		testIncludedClibraryFromJNA();
		
		testJavaCalculatorFromJNIDemoButWithJNA();
		
	}

	
	
	//----------------------- SIMPLE DEMO WITH EMBEDDED C LIBRARY

	private static void testIncludedClibraryFromJNA() {
		System.out.println("Escribe algo:");
		
		byte[] jnaInput = new byte[32];
		
		//ya con la instancia de Library podemos acceder a los métodos escritos en código nativo
		StandardAccess.INSTANCE.scanf("%s", jnaInput);
		StandardAccess.INSTANCE.printf("%s [%s] %s %s!\n", "Your message", Native.toString(jnaInput), "is printed thanks to the JNA library version", "5.5.0");
	}
	
    public interface StandardAccess extends Library {
    	
    	//en vez de System.load o loadLibrary
        StandardAccess INSTANCE = Native.load("c", StandardAccess.class); // maps the shared library (.so, .dll) to a Library interface.

        int scanf(String format, Object ... args);
        void printf(String format, Object ... args);
        
        /* Then we can map signatures of native methods we need to use to Java methods in order to access them.
         * Here’s the list of type mappings: ( https://github.com/java-native-access/jna/blob/master/www/Mappings.md )
         * */
    }

    
    
    //----------------------- SIMPLE DEMO WITH ANOTHER NATIVE CODE
    
    // se hizo una clase c diferente, pq en JNI la estructura sigue una sintáxis especial por los headers
    
    // If we take a look at the native library that we’re calling, you will realize it’s a lot simpler without JNI stuff embedded in it.
    // It’s basically plain C and the code has no idea that some Java developer might call it someday!
    
    public static final String ABSOLUTE_LIB_DIR = "src/main/java/com/JNA/leo/jnaexample/";
    public static final String LIBRARY_NAME = "libcalculator.so"; // .so on Unix or .dll on Windows
    
    private static void testJavaCalculatorFromJNIDemoButWithJNA() {

    	JavaCalculatorJNAInterface jnaInterface = JavaCalculatorJNAInterface.INSTANCE;
    	
    	int num = 20;
        int num2 = 10;

        int sum = jnaInterface.sum(num, num2);
        int mult = jnaInterface.multiply(num, num2);
        String greeting = jnaInterface.sayHello();

        System.out.println(num + " + " + num2 + " = " + sum);
        System.out.println(num + " x " + num2 + " = " + mult);
        System.out.println(greeting);
    	
    }
    
    public interface JavaCalculatorJNAInterface extends Library {
    	
    	JavaCalculatorJNAInterface INSTANCE = (JavaCalculatorJNAInterface) Native.load(
    			new File(ABSOLUTE_LIB_DIR + LIBRARY_NAME).getAbsolutePath(), 
    			JavaCalculatorJNAInterface.class);
    	
    	int sum(int a, int b);

        int multiply(int a, int b);

        String sayHello(); // podemos retornar objetos incluso! (que contengan otros objetos o primitivos o enums)
    	
    }
    
    
    
    
    
    /** *** SUPONGAMOS QUE ALGÚN MÉTODO RETORNA UN OBJETO
     * 
     * Con JNI:
     * Teníamos clases Java normales, y para poder manipular eso desde C había que usar reflexión en el wrapper .c a partir del .h generado.
     * 
     * Con JNA:
     * Extendemos de Structure.
     * As you can see, we need to extend Structure class, which maps native c structs to Java classes.
     * We will be returning the struct by its value 
     * and Structure assumes it’s returned by reference, 
     * so we will also implement Structure.ByValue interface in order to fix that.
     * 
     * CLASE NATIVA EN C AL FINAL
     */
    public class TemperatureData extends Structure implements Structure.ByValue {
    	// Instead of using Strings, we will use byte arrays and convert them to String using Native.toString() method.
    	// we can also use const char[], but to avoid string encoding errors it's better byte[].
    	public byte[] timestamp = new byte[128];
        public float temperature;
        public byte[] scale = new byte[16];// originally was enum, but there is no mapping in JNA, so we then convert from let's say string to enum

        // we define our field order to match native struct field declaration order.
        @Override
        protected List getFieldOrder() {
            return Arrays.asList("timestamp", "temperature", "scale");
        }
        @Override
        public String toString() {
            return String.format("Timestamp = %s\nTemperature = %f\nScale = %s", Native.toString(timestamp), temperature, Native.toString(scale));
        }
    }
    
}

// If we take a look at the native library that we’re calling, you will realize it’s a lot simpler without JNI stuff embedded in it.
// It’s basically plain C and the code has no idea that some Java developer might call it someday!
// nos olvidamos del .h y .c wrapper

/* libtemperaturesampler.so

#include <stdio.h>
#include <string.h>

struct DetailedTemperature {
    char timestamp[128];
    float temperature;
    char scale[16];
};

float getTemperature() {
    setvbuf(stdout, NULL, _IONBF, 0);
    printf("%s\n","Returning Simple Temperature...");
    return 27.8;
}

struct DetailedTemperature getDetailedTemperature() {
    setvbuf(stdout, NULL, _IONBF, 0);
    struct DetailedTemperature detailedTemperature;

    strcpy(detailedTemperature.timestamp, "02-03-2020 17:30:48");
    detailedTemperature.temperature = 27.8;
    strcpy(detailedTemperature.scale, "CELCIUS");

    printf("%s\n","Returning Detailed Temperature...");
    return detailedTemperature;
}
 * */


