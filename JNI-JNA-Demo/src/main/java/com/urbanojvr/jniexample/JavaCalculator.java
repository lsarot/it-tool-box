package com.urbanojvr.jniexample;

import java.io.File;

/**
 * 			JNI DEMO
 * */

public class JavaCalculator {
	
	public static final String ABSOLUTE_LIB_DIR = "src/main/java/com/urbanojvr/jniexample/";
    public static final String LIBRARY_NAME = "libcalculator.so"; // .so on Unix or .dll on Windows

    /**
     * Los métodos en C se deben wrappear en una clase Java, pero a modo de interfaz, usando native y sin implementación.
     * Esto permite traducir los argumentos desde el mundo Java al mundo C.
     * 
     * Sobre esta clase wrapper, debemos crear el header (.h), hacemos entonces:
     * 
     * Javac JavaCalculator.java -h .
     * 
     * Esto nos creará un archivo cuyo nombre estará basado en la estructura de paquetes de nuestra aplicación.
     * 
     * continúa al fondo de la clase.
     * */
    public native int sum(int a, int b);

    public native int multiply(int a, int b);

    public native String sayHello(String name); // podemos retornar objetos incluso! (que contengan otros objetos o primitivos o enums)

    /**
     * Creamos un archivo .c que incluya el archivo .h mostrado antes con la lógica de las funciones. Le he llamado igual que su clase cabecera, para que quede claro que es la implementación de nuestro wrapper.
     * 
     * Se usa el .h como template, pero esta vez tienen la implementación del método. El .h es una interfaz.
     *  
     *  COMPILAR EL CÓDIGO C/C++:
     *  
     *  Como depende de la plataforma, varía para cada una!
     *  Para Android usamos NDK.
     *  Para Unix-based GCC.
     *  	.primero como un objeto común (.o)
     *  	.luego como un shared object (.so) para poder ser importado en otro proyecto.
     *  
     *  gcc -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin com_urbanojvr_jniexample_JavaCalculator.c -o com_urbanojvr_jniexample_JavaCalculator.o
     * 
     *  y a partir del .o, generamos el .so:
     *  
     *  gcc com_urbanojvr_jniexample_JavaCalculator.o -shared -o libcalculator.so
     *  
     *  finalmente, cargamos la librería nativa desde Java.
     *  
     *  
     *  PARA WINDOWS, UN EJEMPLO DE UN SCRIPT AL FINAL DE LA CLASE!
     *  Y CON OBJETOS COMPLEJOS, no primitivos. USARÁ REFLEXIÓN EN C.
     *  Y MUESTRA CÓMO LLAMAR DESDE C A MÉTODOS O ENUMS EN JAVA.
     * */

    static {
    	System.load(new File(ABSOLUTE_LIB_DIR + LIBRARY_NAME).getAbsolutePath());
    	
    	/* loadLibrary Vs load:
    	 * para hacerlo fácil, creamos la librería con el nombre libMILIBRERIA.so y usamos load, con la ruta absoluta dentro del proyecto.
    	 * 
    	 * Si usamos loadLibrary, se añadirá lib y .so al inicio y al final del nombre que coloquemos.. 
    	 * ie. loadLibrary("calculator")
    	 * debiéramos guardar el .so en dir donde estén las libs de Java (java.library.path) y se buscará por libcalculator.so
    	 */
    }
    
}


/** PARA WINDOWS VIMOS UN SCRIPT (.sh) Y LO COMENTAMOS:

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export OUT_DIR=${PWD}/out		# es dir_actual/out
export CPP_FOLDER=${PWD}/cpp
export PROJECT_PACKAGE=${PWD}/java/com/jni/example
export MY_JNI_HEADERS=${OUT_DIR}/jniHeaders

temperaturesampler:
	# crea las carpetas
	mkdir -p ${OUT_DIR} ${MY_JNI_HEADERS}
	# compila las clases usadas y genera los headers, a ambos les pone el destination path
	javac -h ${MY_JNI_HEADERS} -d ${OUT_DIR} ${PROJECT_PACKAGE}/TemperatureSampler.java ${PROJECT_PACKAGE}/TemperatureData.java ${PROJECT_PACKAGE}/TemperatureScale.java
	# creamos el .o
	# a dif del ej en Unix-based, este incluyó los headers en la compilación.
	# notar que usamos g++ y .cpp, y no gcc y .c
	g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux -I${MY_JNI_HEADERS} ${CPP_FOLDER}/TemperatureSampler.cpp -o ${OUT_DIR}/temperatureSampler.o
	# creamos el .so para importar a proyectos Java que se ejecuten en Windows claro.
	g++ -shared -fPIC -o ${OUT_DIR}/libtemperaturesampler.so ${OUT_DIR}/temperatureSampler.o -lc

# lo siguiente no es necesario, nos basta con tener el .so para importar al proyecto Java.

run_temperaturesampler: temperaturesampler
	java -cp ${OUT_DIR} -Djava.library.path=${OUT_DIR} com.jni.example.TemperatureSampler

clean:
	rm -rf ${OUT_DIR} ${MY_JNI_HEADERS}
 * */



/* *** SUPONGAMOS QUE NUESTRO RETURN TYPE ES LA CLASE TemperatureData, Y TRABAJA CON TemperatureScale QUE ES UN ENUM. TENDREMOS QUE USAR REFLEXIÓN!
 * nuestro código C/C++:

*** from   https://medium.com/swlh/introduction-to-java-native-interface-establishing-a-bridge-between-java-and-c-c-1cc16d95426a

JNIEXPORT jobject JNICALL Java_com_jni_example_TemperatureSampler_getDetailedTemperature (JNIEnv * env, jobject thisObject) {

    // Get the TemperatureData and create an instance of it.
    jclass temperatureDataClass = env->FindClass("com/jni/example/TemperatureData");
    jobject temperatureData = env->AllocObject(temperatureDataClass);

    // We only need TemperatureScale class here, enumeration will be created later.
    jclass temperatureScaleClass = env->FindClass("com/jni/example/TemperatureScale");

    // Get fields of TemperatureData
    jfieldID timestamp = env->GetFieldID(temperatureDataClass, "timestamp", "Ljava/lang/String;");    //Un Array sólo antepones [ (ie. [Ljava/lang/String or [F for float) ... Z (boolean), B (byte), I (int), L (any non-primitive), etc.
    jfieldID temperature = env->GetFieldID(temperatureDataClass, "temperature", "F");
    jfieldID scale = env->GetFieldID(temperatureDataClass, "scale", "Lcom/jni/example/TemperatureScale;");

    // Get CELCUIS scale from TemperatureScale enum.
    jfieldID scaleEnumID = env->GetStaticFieldID(temperatureScaleClass, "CELCIUS", "Lcom/jni/example/TemperatureScale;");
    jobject celciusScale = env->GetStaticObjectField(temperatureScaleClass, scaleEnumID);

    // Check if CELCIUS is the supported scale.
    jclass callerClass = env->GetObjectClass(thisObject);
    jmethodID preferredScaleMethodID = env->GetMethodID(callerClass, "getPreferredScale", "()Lcom/jni/example/TemperatureScale;");    //(params)returnType...    if we had 2 params, our method signature would look like	“(Ljava/lang/String;F)Lcom/jni/example/TemperatureScale;”
    jobject preferredScale = env->CallObjectMethod(thisObject, preferredScaleMethodID);

    if (!env->IsSameObject(preferredScale, celciusScale)) {
        std::cout << "Preferred scale is not supported, using CELCIUS instead!" << std::endl;
    }

    // Set TemperatureData fields.
    env->SetObjectField(temperatureData, timestamp, env->NewStringUTF("02-03-2020 17:30:48"));
    env->SetFloatField(temperatureData, temperature, 27.8);
    env->SetObjectField(temperatureData, scale, celciusScale);

    std::cout << "Returning Detailed Temperature..." << std::endl;
    return temperatureData;
}

 * */



