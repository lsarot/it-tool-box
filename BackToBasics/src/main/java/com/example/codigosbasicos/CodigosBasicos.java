package com.example.codigosbasicos;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.codigosbasicos.arbol.BinaryTree;
import com.example.codigosbasicos.enums.TipoDePiedra;
import com.example.codigosbasicos.enums.Vehiculo;
import com.example.codigosbasicos.herencia.HijoA;
import com.example.codigosbasicos.herencia.Padre;
import com.example.codigosbasicos.sincronizacion.SyncDemo;
import com.example.codigosbasicos.sincronizacion.TestThread;
import com.example.codigosbasicos.sockets.ClientSocketHandler;
import com.example.codigosbasicos.sockets.ObjetoSerializable;
import com.example.codigosbasicos.sockets.ServerSocketHandler;
import com.example.codigosbasicos.util.ShutdownHookThread;
import com.example.codigosbasicos.util.TestThread2;
import com.example.codigosbasicos.wait_notify_notifyall.BlockingVector;
import com.example.codigosbasicos.wait_notify_notifyall.PutThread;
import com.example.codigosbasicos.wait_notify_notifyall.TakeThread;

//PRINCIPIOS DE LA POO
//ABSTRACCIÓN: una clase abstrae un tipo de objeto, pero el objeto se consigue instanciando la clase.
	//interfaces and abstract classes
//ENCAPSULAMIENTO: una clase posee miembros propios que pueden ser accedidos bajo la permisología otorgada en la definición de la clase.
//HERENCIA: una clase puede heredar sus miembros a otras clases.
//POLIMORFISMO: un objeto puede ser de diferente tipo, según los que sean sus hijos en la herencia... algunos autores dicen que el polimorfismo viene de poder nombrar un método igual en varias clases, pero hacen cosas distintas, refiriéndose expecíficamente a los métodos sobreescritos.
	//overwrite and overload methods and constructors

public class CodigosBasicos {
 
 public static String sep = "-------------------------------------------------- > ";
 
 public static void main(String[] args) {
     //DESDE UN MÉTODO STATIC NO PUEDO LLAMAR UN 'MÉTODO' Ó 'VARIABLE EXTERNA AL MÉTODO' NON-STATIC
     //POR ELLO CREAMOS UNA INSTANCIA
     CodigosBasicos cb = new CodigosBasicos();
     cb.inicio();
 }
 
 public void inicio() {
     /*--------------------------------------- CONFIGURAR VARIABLES DE ENTORNO Y ELEGIR VERSIÓN DE JAVA A USAR
     -PARA CHEQUEAR LA VERSIÓN DE JAVA EN CMD WINDOWS Y TERMINAL MACOS
     java -version 
     -PARA CHEQUEAR LA VERSIÓN DEL COMPILADOR EN WINDOWS Y MAC
     javac -version

     -EN VARIABLES DE ENTORNO DEBE EXISTIR LA RUTA DONDE JAVA BUSCARÁ SU JRE, ESTO ES EN PATH.
     -SI ESTÁ ESCRITO ALGO COMO C:\Program Data\Oracle\Java\javapath ES UNA RUTA DONDE AUTOMÁTICAMENTE SE GUARDA LA ÚLTIMA VERSIÓN DE JAVA.
     -CON ESTO SERÁ SUFICIENTE

     -SI QUEREMOS FIJAR OTRA VERSIÓN DE JAVA (PRINCIPALMENTE ESTO LO USAN PROGRAMAS TERCEROS COMO IDEs):

     -(PARA EL JDK) PODEMOS CREAR LA VARIABLE DE ENTORNO JAVA_HOME=C:\Program Files\Java\jdk1.8.0.121 Y EN PATH PODEMOS USAR %JAVA_HOME%\bin COMO UN COMODÍN EN LUGAR DE LA RUTA COMPLETA, PERO DEBEMOS COLOCAR JAVA_HOME PRIMERO EN LA LISTA… ESTO FUNCIONARÁ PQ LA CARPETA C:\Program Files\Java\ TAMBIÉN GUARDA EL JRE.

     -(PARA EL JRE) PODEMOS CREAR LA VARIABLE DE ENTORNO JRE_HOME=C:\Program Files\Java\jre1.8.0.121\bin

     Debemos reiniciar Windows si es primera vez que instalamos el JDK para que CMD reconozca el comando javac.

     NOTA:

     JAVA FUNCIONA MEJOR CON EL JDK QUE CON EL JRE PARA EJECUTAR PROGRAMAS.
     ESTO PQ JRE INSTALA EL ‘JAVA HOTSPOT CLIENT’ EN LUGAR DEL ‘JAVA HOTSPOT SERVER’.*/
     
     /*---------------------------------------------------------------------*/
     
     //TODO EL API JAVA PROVIENE DE java.  o  javax.
     //java.lang se carga automáticamente, por eso podemos usar ciertas instrucciones como:
     java.lang.System.out.println();// pero no hace falta redundar con el java.lang. de prefijo
     
     /*--------------------------------------- MODIFICADORES DE ACCESO*/
     //CLASE TIENE MIEMBROS(MÉTODOS Y ATRIBUTOS[VARIABLES, CONSTANTES])
     //public: (clase, método o atributo) se puede acceder desde cualquier clase
     //private: (clase, método o atributo) se puede acceder sólo desde la misma clase
     //protected: (clase, método o atributo) se puede acceder desde las hijas y las clases del mismo paquete
     //friendly: (clase, método o atributo) se puede acceder sólo desde las clases del mismo paquete. (no se marca)

     //sincronyzable: (clase) a partir de flags, no permite que 2 threads accedan a métodos de una clase al mismo tiempo (suena como semáforos)

     //abstract: (clase y método) clase base para la herencia, debe tener al menos un método abstract. No permite instanciar la clase
     //static: (método o atributo) permite acceder a dicho miembro sin necesidad de instanciar un objeto de la clase. Si es atributo, además cualquier instancia de la clase apuntaría al mismo valor en memoria
     //final: (clase, método o atributo) en clase no permite extender (no herencia), en método no permite Override y, en atributo no permite cambiar valor una vez asignado
     
     /*--------------------------------------- SOBRE VALORES NUMÉRICOS Y OTROS TIPOS PRIMITIVOS*/
     long i1 = 9223372036854775807L;// 64 bits... (-9.223.372.036.854.775.808L, +9.223.372.036.854.775.807L)... se debe colocar la L, pero al mostrar el número no sale la letra. (0L default)
     int i2 = -2147483648;// 32 bits... (-2.147.483.648, +2.147.483.647) (0 default)
     short i3 = 32767;// 16 bits... (-32.768, +32.767) (0 default)
     byte i4 = -128;// 8 bits... (-128, +127) (0 default)
     float f1 = -340282356779733661637539395458142568447.99999999999999999999999999999999999999f;// (acepta -/+ 340282356779733661637539395458142568447.99999999999999999999999999999999999999f) (0.0f default)
     double f2 = 99999999999999999999999999999999999999999999999999999.99999999999999999999999999999999999999d;// (acepta -/+ INFINITUS) (0.0d default)
     char c = '\u0000';// (default)
     boolean b = false;// (default)
     //Reference r = null;// (default)
     
     /*--------------------------------------- REFERENCIAS*/
     //Clase r1 = new Clase();// r1 es una referencia nueva de un objeto (tiene una var global 'public int valor = 100')
     //Clase r2 = r1;// r2 apunta a la misma referencia que r1
     //r2.valor = 0; r2.setValor(0);// modificando una variable desde r2 afectamos r1 (Nota: r2.valor es una referencia !!! r2.setValor() es una llamada a un método)
     //r2.getValor() = 0;// NO EXISTE! pq la llamada a un método siempre debe estar sóla o asignarla a una referencia (a la derecha como sigue el próximo ej.)
     //r2.valor = r2.getValor();

     String s1 = new String("cadena 1");// Con el caso de la clase String podemos explicar por qué no se modifica s1 con s2... Primero debemos saber que un String en java puede inicializarse como 'new String()' que es lo mismo que 'new String("")' ó también con "" ó "texto"
     String s2 = s1;// efectivamente s2 apunta a la referencia de s1
     //la clase String tiene 'private' la variable que guarda la cadena de texto (quizás es un vector de bytes), por eso no podemos modificarlo directamente... tampoco tiene un método accesible como setString() con el cual podamos modificarlo
     s2 = s2.replace('a','o');// la clase sólo tiene métodos getters o que retornan otro String modificado, pero el método no cambia el original... al asignarse el valor retornado a una referencia, lo que hacemos es realmente inicializarla nuevamente, ya que (String s = "String devuelto") está es inicializando
     
     /*--------------------------------------- TIPO ENUMERADO JAVA*/ System.out.println(sep+" TIPO ENUMERADO JAVA");
     /* Por convención, sus nombres se escriben en letras mayúsculas para recordarnos que son valores fijos (que en cierto modo podemos ver como constantes).
      * No se pueden crear más objetos variantes del tipo enumerado que los especificados en su declaración. Tener en cuenta, porque es una confusión habitual, que los tipos enumerados no son enteros, ni cadenas (aunque a veces podamos hacer que se comporten de forma similar a como lo haría un entero o una cadena). Cada elemento de un enumerado es un objeto único disponible para su uso.
      * Un tipo enumerado puede ser declarado dentro o fuera de una clase, pero no dentro de un método.
      */
     Vehiculo vehi = new Vehiculo();
     vehi.setMatricula("AFH-00L");
     vehi.setMarca(Vehiculo.MarcaDeVehiculo.MERCEDES_BENZ);
     System.out.println(vehi.toString());
     System.out.println("Posibles valores:");
     for(Vehiculo.MarcaDeVehiculo tmp : Vehiculo.MarcaDeVehiculo.values()) { System.out.print(tmp.name() + "   "); } System.out.println();
     System.out.println();
     TipoDePiedra piedra = TipoDePiedra.CUARZO;
     System.out.println("Piedra seleccionada: " + piedra.toString() + ", Su Color: " + piedra.getColor() + ", Su Peso: " + piedra.getPeso());
     
     /*--------------------------------------- VARIOS*/ System.out.println(sep+" VARIOS");
     // | ***  ESTE BLOQUE DE CÓDIGOS MUESTRA LA EXISTENCIA DE ALGUNAS CLASES QUE PUDIERAN SER DE UTILIDAD *** |
     //Los métodos toString() y equals(Object obj) de la clase Object, pudieran sobreescribirse para otorgar datos deseados.
     int[][] matrix = new int[3][2];// generalmente se entiende como 3 filas con 2 columnas, debido a la manera de recorrer el arreglo
     int[][] matriz = {{1,2},{3,4}};
     int[][][] matriz3D = new int[3][3][3];// 3 filas, 3 columnas y 3 de profundidad, serían 3x3x3=27 elementos... puede hacerse de 4+ dimensiones también!
     int[][][][][] momento = new int[24][31][12][100][21];// hora, día, mes, año, siglo
     
     //ESCRIBIR EN UN STRING: \b backspace  \t horizontal_tab  \n linefeed  \f form_feed  \r carriage_return  \" double_quote  \' single_quote  \\ backslash
     
     System.out.println("USANDO MÓDULO EN JAVA: (5 % 2) = " + 5%2);// = 1 --> porque 5/2 = 2 y llevo 1
     
     //DEVOLUCIÓN DIRECTA CON COMPARACIÓN PREVIA
     int entero = 10;
     String a = (entero > 6 ? "devuelve_si_true":"devuelve_si_false");
     
     /*  Una buena práctica es llamar variables locales y globales todas distintas, pero dado el caso que se requiera usar un mismo nombre, Java considera usar la local y no la global
         .. 'this' sirve para hacer referencia a este objeto, con él podemos incluso llamar a uno de los constructores (this(<parámetros>);) y también hacer referencia a variables globales y métodos de la clase.
      */
     /*  System.out.print("Escribe algo: ");
         Scanner scanner = new Scanner(System.in); System.out.println("---> " + scanner.nextLine()); 
         //Para leer también sirve System.in.read();
         BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); System.out.println("---> " + reader.readLine());
      */
     /*  Si queremos comparar referencias entre objetos(no tipos primitivos), debemos usar ==; si queremos comparar el contenido, debemos usar .equals
         .equals proviene de Object, debemos Override en una clase para establecer la relación de igualdad que queramos entre 2 objetos
         podemos copiar objetos (básicamente al usar new se crea una nueva ref), pero si en el contenido del original hay referencias a otros objetos(no primitivos), el objeto copia apuntará a los mismos.
      */
     
     //Collator col = Collator.getInstance(Locale.US);   col.setStrength(Collator.Primary);   col.compare("HÛla","hola");
     //StringTokenizer st = new StringTokenizer("cadena");
     //for extendido: for(TipoObjeto <referencia_temporal> : Collection)... i.e. for(String name:list)... un bucle for o while puede detenerse con break;
     //String.valueOf(<parámetro>);   Integer.valueOf(<parámetro>);... valueOf es un método sobrecargado para hacer cast a diferentes tipos de datos Java
     //LA CLASE Arrays PERMITE OPERAR SOBRE ARRAYS ESTÁTICOS Y DINÁMICOS (tiene métodos static)
     
     //LA CLASE SYSTEM POSEE MÉTODOS QUE IMPLEMENTAN MÉTODOS DE LA CLASE RUNTIME Y SUELEN LLAMARSE POR LA VÍA DE SYSTEM
     Runtime runtime = Runtime.getRuntime();// Every Java application has a single instance of class Runtime that allows the application to interface with the environment in which the application is running. The current runtime can be obtained from the getRuntime method... An app cannot create an instance of this class.
     runtime.addShutdownHook(new ShutdownHookThread());
     System.out.println("[Available Processors: "+runtime.availableProcessors() + "][Max.Memory MB: "+(runtime.maxMemory()/1048576) + "][Total.Memory MB: "+(runtime.totalMemory()/1048576) + "][Free.Memory MB: "+(runtime.freeMemory()/1048576)+"]" );
     //runtime.runFinalization();// runs all finalize methods of objects in the currently running JVM
     
     //System.exit(1);
     Properties properties = System.getProperties();
     System.out.print("All System Properties by key: ");
     for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) { System.out.print("\"" + e.nextElement() + "\","); } System.out.println();
     System.out.println("SomeOfThem: " + System.getProperty("java.version") + "; " + System.getProperty("java.home") + "; " + System.getProperty("os.name") + "; " + System.getProperty("os.arch") + "; " + System.getProperty("os.version") + "; " + System.getProperty("file.separator") + "; " + System.getProperty("path.separator") + "; " + System.getProperty("line.separator") + "; " + System.getProperty("user.name") + "; " + System.getProperty("user.home") + "; " + System.getProperty("user.dir"));
     
     SecurityManager secManager = System.getSecurityManager();
     //secManager.check... // lanza una exception si no hay un permiso para xs acción
     
     //Aún no sé funcionalidad!!!!
     try { 
         Class clase1 = Class.forName("java.lang.Thread", true, getClass().getClassLoader());
         //Class clase2 = Class.forName("codigosbasicos.rmi.messageapp.ClientHandler");
     } catch (ClassNotFoundException ex) { Logger.getLogger(CodigosBasicos.class.getName()).log(Level.SEVERE, null, ex); }
     
     
     /*--------------------------------------- OBJETO Y CLASE ANÓNIMA*/
     //Un objeto anónimo es aquél que se envía como parámetro, pero sin antes declararlo. i.e. metodo( new Objeto() ); .. entonces no se puede utilizar luego sino en el método enviado.
     
     //Una clase anónima es aquella que no está definida en un archivo explícitamente, ya sea anidada o sóla, sino que se envía como un parámetro su definición
     // i.e. cuando se utiliza boton.setOnClickListener(...); en lugar de enviar una instancia de una clase que implemente la interfaz onClickListener
     // se hace boton.setOnClickListener(new onClickListener() {
     //              @Override
     //              public void metodo() {...}
     //         } );
     //.. entonces estamos definiendo una clase anónima que implementa la interfaz onClickListener y sobreescribe su método metodo()
     //.. nótese que debe existir la interfaz onClickListener o la clase que pongamos, en cuyo caso de ser una clase, se entiende que la definición
     //.. es una clase anónima que hereda de dicha clase.
     
     /*--------------------------------------- RECURSIVIDAD CON EJEMPLO*/ System.out.println(sep+" RECURSIVIDAD CON EJEMPLO");
     
     System.out.print("Perfect numbers from 1 to 100: ");
     for(int i=0;i<100;i++) {
         if(isPerfect(i+1)) System.out.print((i+1)+" is perfect!...");//nros del 1 al 100 perfectos (la suma de los divisores de N es igual a N, sin contar a N como divisor)
     } System.out.println();
     
     //--------------------------------------- ORDENAMIENTOS BÁSICOS: 3 MÉTODOS
     //ordenarSeleccion(); //ordenarInsercion(); //ordenarBurbuja();
     
     /*--------------------------------------- ÁRBOL BINARIO*/ System.out.println(sep+" ÁRBOL BINARIO");
     int[] vec = {0,-20,20,-30,-10,-25,-15,-4,-6,-5,-8,15,30,25,40};
     BinaryTree bt = BinaryTree.llenarYretornarArbolConEnteros(vec);
     BinaryTree.inOrder(bt.getRoot()); System.out.println();
     System.out.println("Cantidad nodos: " + BinaryTree.countNodes(bt.getRoot()) + "; Hojas: " + BinaryTree.countLeafs(bt.getRoot()));
     System.out.println("HighestNode: "+BinaryTree.findHighestNode(bt.getRoot()).getId()+", LowestNode: "+BinaryTree.findLowestNode(bt.getRoot()).getId()+", TreeHeight: "+BinaryTree.getHeight(bt.getRoot(), 1));
     int level = 3; System.out.print("VisitLevel "+level+": "); BinaryTree.visitLevel(bt.getRoot(), level, 0); System.out.println();
     //bt.pruneTree(bt.getRoot()); BinaryTree.inOrder(bt.getRoot()); System.out.println();
     //bt.removeSubTree(BinaryTree.getNodeOrderedTree(20, bt.getRoot())); BinaryTree.inOrder(bt.getRoot()); System.out.println();
     bt.imprimeArbolEjercicio();
     
     /*--------------------------------------- LISTA SIMPLE, DOBLE, CIRCULAR... PILA Y COLA... IMPLEMENTACIONES JAVA*/ System.out.println(sep+" LISTA SIMPLE, DOBLE, CIRCULAR... PILA Y COLA... IMPLEMENTACIONES JAVA");
     /* - Lista Simple, Doble y Circular están desarrollados en las carpetas de estructura de datos, PERO EL API JAVA INCLUYE MEJORES CLASES PARA MANEJAR ESTRUCTURAS DE OBJETOS
      
      * - Java ofrece Apis que derivan de las Interface Collection y Map del package java.util 
      * - Una colección de objetos no puede ser recorrida y a la vez modificada, para ello disponemos de la herramienta Iterator
      * Iterator<String> ite = lista.iterator();
      * ite.hasNext();(usualmente trabaja con while)  ite.next();  it.remove(); 
      */
     List<String> arrayList = new ArrayList<>();
     List<String> linkedList = new LinkedList<>();
     for(int i=0;i<10000;i++) { arrayList.add("cadena"); linkedList.add("cadena"); }// inicializamos ambos arrays con 10 mil elementos
     long antes = System.nanoTime(); arrayList.add(0, "cadena"); System.out.println("Inserción inicio ArrayList: " + (System.nanoTime()-antes) + " nanosec");
     antes = System.nanoTime(); linkedList.add(0, "cadena"); System.out.println("Inserción inicio LinkedList: " + (System.nanoTime()-antes) + " nanosec");
     antes = System.nanoTime(); arrayList.add(5000, "cadena"); System.out.println("Inserción en medio ArrayList: " + (System.nanoTime()-antes) + " nanosec");
     antes = System.nanoTime(); linkedList.add(5000, "cadena"); System.out.println("Inserción en medio LinkedList: " + (System.nanoTime()-antes) + " nanosec");
     antes = System.nanoTime(); arrayList.add("cadena"); System.out.println("Inserción final ArrayList: " + (System.nanoTime()-antes) + " nanosec");
     antes = System.nanoTime(); linkedList.add("cadena"); System.out.println("Inserción final LinkedList: " + (System.nanoTime()-antes) + " nanosec");
     // List<String> vec = new Vector<>(); es apropiado para ambientes multihilo por ser sincronyzed. Se recomienda ArrayList a menos que sea necesario trabajar en multihilo sobre el arreglo
     //Probar HashSet y TreeSet que implementan interfaces Set y SortedSet respectivamente
     SortedMap<Integer,String> smap = new TreeMap<>();
     int key; for(int i=0;i<10000;i++) { key=i; if(i%2!=0)key=-i; smap.put(key,"cadena"); }// insertamos 10 mil items con igual cantidad positivos que negativos
     antes = System.nanoTime(); smap.put(10000,"cadena"); System.out.println("Inserción en TreeMap: " + (System.nanoTime()-antes) + " nanosec");
     
     /*--------------------------------------- HERENCIA*/ System.out.println(sep+" HERENCIA");
     /* El simple hecho de usar 'extends Padre' en clase 'Hija', cumple con lo básico de la herencia, donde Hija puede acceder a los atributos/miembros (métodos y/o variables) de Padre (siempre que tengan la etiqueta de acceso adecuada, public o protected)
      * Ahora, la clase Padre puede utilizar la etiqueta abstract, con la cual:
      *    - No se puede instanciar Padre
      *    - Si se codifica un constructor en Padre que reciba algún parámetro, debemos en cada Hija llamar al constructor de Padre y enviar dicho parámetro, usando super(parámetro)
      *    - Las clases hijas deberán implementar (@Override) todos los métodos abstract de la clase Padre (si los hay) (métodos abstract no tienen código)
      *         - Supóngase que existe una clase Abuelo o Bisabuelo o Tatarabuelo o Dios, si dicha clase es abstract y NO queremos implementar su(s) método(s) en la Hija inmediata sino en otra más jóven(por así llamarla), deberemos etiquetar a las intermedias como abstract (sin implementar el método)
      *         - La clase etiquetada entonces como abstract, deberá poseer método abstract o, servir de intermediaria entre una superior y una inferior
      *         - Por supuesto una clase puede ser abstract, tener sus propios métodos abstract, @Override métodos abstract de alguna superior aún no implementados en el camino, y/o servir de puente entre alguna superior y alguna inferior
      *    - La finalidad de usar abstract es, básicamente, que a través de un objeto general como Padre, podemos llamar al método abstract y recaerá en la definición de la clase hija... siendo una forma de hacer callbacks
      * Sea Padre abstract o no, podemos asignar a un objeto Padre cualquiera de sus clases Hija. ( Padre p = hija; )
      *    - Si queremos llamar un método específico de Hija, debemos convertir el objeto Padre en un tipo Hija, pero debemos saber qué tipo es, para convertir hacemos cast ( HijaA h = (HijaA) p; )
      *    - Podemos revisar su tipo con la instrucción instanceOf que devuelve true si pertenece a ese tipo de Hija ( if(p instanceof HijaA) )
      *    - En este caso es bueno usar abstract, puesto que no sabemos si alguien extenderá de Padre y con qué nombre lo hará. Entonces usando el método abstract de Padre, la llamada recae en el de la clase Hija que no conocemos, sin necesidad de transformar la clase Padre a la Hija
      */
     //Padre p = new Padre(); NO permitido pq es abstract
     Padre p = new HijoA(1);
     p.metodoPadre1();// con p podemos acceder a los métodos de p solamente, no a los de su HijoA
     p.metodoPadre2();// este método recae en HijoA donde fue override, claro está al ser p de tipo HijoA, si p fuera tipo Padre recaería directo en su método
        /* Aquí entran los términos tipo estático y dinámico; para Java p es estáticamente tipo Padre, pero p dinámicamente contiene un objeto tipo HijoA
         * Java buscará el método metodoPadre2() en el tipo dinámico de p, si no lo encuentra buscará en su padre inmediato y así sucesivamente hasta encontrarlo
         * como el método fue Override en HijoA lo encuentra a la primera */
     p.metodoAbstractPadre();// llamamos a un abstract de Padre que recaerá en el codificado en HijoA
     HijoA ha = null;
     if (p instanceof HijoA) { ha = (HijoA) p; }
     ha.metodoHijoA();// ahora podemos llamar a un método de HijoA
     ha.metodoPadre1();// también podemos llamar a un método de Padre, pero como se puede notar, debimos saber los posibles tipos que puede ser Padre
     ha.metodoPadre2();// llamo directamente a un método de HijoA
     ha.metodoAbstractPadre(); // podemos llamar incluso al método abstract de Padre que recaerá en el del Hijo
     p = ha;// podemos convertir sin problemas de Hija a Padre, pero de Padre a Hija deberá chequearse el tipo con instanceof y hacer casting
     
     //--------------------------------------- INTERFACE
     /* Se diseñó para simular una herencia múltiple en Java
      *    - Todo método es public abstract (no hace falta identificarlo)
      *    - No hay ningún constructor
      *    - Sólo admite campos public static final... buen lugar para guardar constantes del programa
      * Si una clase implements Interface, la clase debe sobreescribir los métodos de la interface o, declararse abstract en caso contrario, pero si se quiere extender de esta, se debe implementar los métodos en la nueva clase ya que en la padre no fueron implementados
      * Si necesitamos que una clase herede de varias superclases, estas superclases son candidatas a ser Interface
      * Podemos definir un objeto del tipo Interface, pero sólo se puede instanciar con una clase que implemente dicha Interface... ej: List<String> lista = new LinkedList<>();
      */
     // Interfaces ppales. de java.lang: Cloneable, Comparable<TipoDeClase>, Iterable<TipoDeClase>
     // Cuando queramos implementar Cloneable debemos tener en cuenta que una estructura que contenga otro objeto dentro es más difícil, i.e. un BinaryTree tiene un nodo root, entonces BinaryNode también debe implementar método clone para clonarlo y no que haya dos árboles distintos, pero cuyas roots apunten al mismo objeto... En el caso de haber herencia también debemos considerar hacer uso de super.clone();
     
     //--------------------------------------- ARCHIVOS DE ACCESO ALEATORIO, DE TEXTO Y DE OBJETOS
     //Desarrollo decente del tema en el proyecto de tesis GestorTurnos
     // Todas las clases de este tema están en el paquete 'java.io'
     //TEXTO
     //File   FileReader    FilerWriter
     //RANDOM
     //RandomAccessFile("nombreArchivo","modo:r,rw")      RandomAccessFile(File,"mode")
     //OBJETO
     //SI UNA CLASE ES SERIALIZABLE, TODAS SUS SUBCLASES TAMBIÉN LO SERÁN
     //ATRIBUTOS TRANSIENT Y STATIC NO SE SERIALIZAN
     
     /*--------------------------------------- INMUTABLE Y MUTABLE. CASO STRING*/ System.out.println(sep+" INMUTABLE Y MUTABLE. CASO STRING");
     /* En Java existen 2 tipos de objetos desde el punto de vista de la memoria, los mutables son aquellos que al modificar se cambia el contenido en memoria donde estaba, mientras que los inmutables realmente dejan intacto el contenido en memoria y la referencia pasa a apuntar a un nuevo objeto.
      * En Java, la mayoría de los objetos "esenciales" son inmutables: Long, Integer, Boolean, String, etc.
      * StringBuffer es tipo mutable, podemos usarla en mejora de la clase String
      * StringBuilder ofrece funciones análogas a StringBuffer, pero no usa métodos sincronizados por lo que pudiera generar inconsistencia en programas multihilo, pero tiene mejor rendimiento. Este rendimiento frente a un simple String se notará sólo si trabajamos con miles de operaciones sobre String.
      */
     StringBuffer sbuffer = new StringBuffer();
     long inicio1 = System.currentTimeMillis(); for (int i=0; i<10000; i++) { sbuffer.append("cadena"); } long fin1 = System.currentTimeMillis(); System.out.println("Tiempo del StringBuffer en concatenar N StringBuffer: " + (fin1-inicio1) + " milisec");       
     StringBuilder sbuilder = new StringBuilder();
     long inicio2 = System.currentTimeMillis(); for (int i=0; i<10000; i++) { sbuilder.append("cadena"); } long fin2 = System.currentTimeMillis(); System.out.println("Tiempo del StringBuilder en concatenar N StringBuilder: " + (fin2-inicio2) + " milisec");
     String string = new String();// la instrucción '+' en String Java la compila como un new StringBuilder, creando un nuevo objeto en el heap de memoria... al hacer muchas operaciones se crean muchos nuevos objetos y JVM deberá ir vaciando memoria y es por eso que tarda demasiado esta instrucción y no es así con StringBuilder.append o StringBuffer.append
     long inicio3 = System.currentTimeMillis(); for (int i=0; i<10000; i++) { string = string.concat("cadena"); } long fin3 = System.currentTimeMillis(); System.out.println("Tiempo del String en concatenar N String: " + (fin3-inicio3) + " milisec");
     /* PARA 10 MILLONES DE CONCATENACIONES: 280milisec, 220milisec, (n/a) 
      * PARA 1 MILLÓN: 50milisec, 40milisec, 25minutos
      * PARA 100 MIL: 8milisec, 6milisec, 12segundos... si en lugar de usar '+' para concatenar Strings uso .concat el tiempo se reduce de 12 a 8 segundos!
      * PARA 10 MIL: 1milisec, 1milisec, 130milisec(usando .concat)... NO USAR STRING A MENOS QUE SE TRABAJE CON MENOS DE 10 MIL OPERACIONES POR EJEMPLO
      * LOS STRING SON GUARDADOS EN CONSTANT STRING POOL, donde se crea un sólo String por cada valor existente. Si creo un nuevo String con una cadena que ya está en este pool, simplemente se apunta su ref a este espacio en memoria, así no se desperdicia mucha memoria... al tratar de cambiar el valor de una ref String, se crea un nuevo objeto si no existe en el pool, pero nunca se cambia el original!
      */
     
     //--------------------------------------- SEMÁFOROS
     /* Los semáforos sirven para gestionar las peticiones de un recurso limitado, como sería el caso de manejar N cantidad de Threads simultáneamente para que el sistema no colapse.
      * Java provee de un Api para utilizar semáforos, java.util.concurrent.Semaphore
      */
     Semaphore semaf = new Semaphore(20,true);
     /* El semáforo debería usarse dentro de un objeto como un pool de items disponibles, una forma de inicializarlo es con el número de items disponibles y un
      * ... fairness value (true/false) que indica si el orden en que se solicita el recurso será respetado o no... se trabaja con semaforo.adquire y semaforo.release,
      * ... leer más en la documentación de la clase!
      */
     
     //--------------------------------------- INNER(NESTED) CLASS/INTERFACE 
     /* En Java podemos crear una class/interface interna, estas SI pueden ser static a diferencia de las más exteriores que no pueden serlo.
      * Para extender/implementar de dicha class/interface sólo hace falta usar extends/implements (claseExterna/InterfaceExterna).(claseInterna/interfaceInterna) sin usar los '()'.
      * - Para crear una instancia de dicha clase hace falta escribir la sintáxis como sigue:
      * ClaseExterna cE = new ClaseExterna();
      * ClaseExterna.ClaseInterna cI = cE.new ClaseInterna();
      * - Para crear una instancia de dicha interface hace falta escribir la sintáxis como sigue:
      * ClaseExterna.InterfaceInterna iI = new ClaseInterna.InterfaceInterna(){ ...@Override algún método si es necesario! };
      * 
      * > Si en la declaración(no inicialización) usáramos sólo ClaseInterna cI en vez de ClaseExterna.ClaseInterna cI, aunque ya importemos ClaseExterna deberemos importar la claseInterna.
      * > Igualmente, si usáramos FULLY QUALIFIED CLASS NAMES no haría falta colocar los import, i.e. supóngase queremos declarar un objeto tipo BinaryTree del package codigosbasicos.arbol, en lugar de hacer import de dicho package, podemos sólo usar (codigosbasicos.arbol.BinaryTree bt)
      */
     
     /*--------------------------------------- SYNCHRONIZED & THREADS*/ System.out.println(sep+" SYNCHRONIZED");
     /* Synchronized methods enable a simple strategy for preventing thread interference and memory consistency errors: if an object is visible to more than one thread, all reads or writes to that object's variables are done through synchronized methods.
      * In a very, very small nutshell: When you have two threads that are reading and writing to the same 'resource', say a variable named foo, you need to ensure that these threads access the variable in an atomic way. Without the synchronized keyword, your thread 1 may not see the change thread 2 made to foo, or worse, it may only be half changed. This would not be what you logically expect.
      * Más información dentro del ejemplo:
      */
     SyncDemo demo = new SyncDemo();// probar a quitar la keyword synchronized en esta clase, los 3 threads accederán en simultáneo al demo instance.
     new TestThread("THREAD 1",demo);
     new TestThread("THREAD 2",demo);
     new TestThread("THREAD 3",demo);
     try { Thread.sleep(500); } catch (Exception e){}
     System.out.println("\n" + sep+" THREADS");
     //-------------
     TestThread2 objRunnable = new TestThread2(); 
     Thread tT2 = new Thread(objRunnable);// Un thread podemos inicializarlo con un objeto que implements Runnable
     tT2.setPriority(Thread.MAX_PRIORITY);// (1,5,10 son las priority por defecto)
     tT2.setDaemon(false);// Se fulmina este tipo de threads sin que se terminen de ejecutar, a diferencia de los user threads
     System.out.println("             >> tT2's state: " + tT2.getState().toString());// revisar los diferentes Thread.State values
     System.out.println("             >> tT2's is Alive?: " + tT2.isAlive());
     tT2.start();
     
     System.out.println("             >> tT2's state: " + tT2.getState().toString());
     System.out.println("             >> tT2's is Alive?: " + tT2.isAlive());
     try { tT2.checkAccess(); } catch (SecurityException se) { /*Este thread no tiene permiso a tT2!*/ }
     try { tT2.join(4000); } catch (InterruptedException ex) {}// Esperar máx n segundos por tT2 para seguir
     
     System.out.println("             ---> Luego de esperar hasta n segundos por la culminación de tT2 imprimimos esto <--- ");
     System.out.println("             ---> tT2's state: " + tT2.getState().toString());
     System.out.println("             ---> tT2 is Alive?: " + tT2.isAlive());
     System.out.println("             ---> tT2 is interrupted?: " + tT2.isInterrupted() + "\n");
     //-------------
     Thread tT3 = new Thread(new TestThread2()); tT3.start();
     tT3.interrupt();// Aunque aparentemente termina su ejecución, arroja una exception al finalizar!
     System.out.println("             ---> tT3's state: " + tT3.getState().toString());
     System.out.println("             ---> tT3 is Alive?: " + tT3.isAlive());
     System.out.println("             ---> tT3 is interrupted?: " + tT3.isInterrupted());
     tT3.interrupt();
     System.out.println("             ---> tT3's state: " + tT3.getState().toString());
     System.out.println("             ---> tT3 is Alive?: " + tT3.isAlive());
     System.out.println("             ---> tT3 is interrupted?: " + tT3.isInterrupted());
     //Usar object.finalize() para culminar un thread sin dejar bloqueo alguno sobre recursos... este método se puede sobreescribir para realizar otras opers. al finalizar.
     try { tT3.join(); } catch (InterruptedException ex) {}
     
     /*--------------------------------------- WAIT, NOTIFY & NOTIFY ALL*/ System.out.println(sep+" WAIT, NOTIFY & NOTIFY ALL");
     // The wait() and notify() methods are designed to provide a mechanism to allow a thread to block until a specific condition is met.
     //CONSIDERAR usar semáforo en lugar de estos mecanismos si es posible!
     BlockingVector blockV = new BlockingVector(1);
     //JUGAR CON EL ORDEN DE ESTAS INSTRUCCIONES
     new PutThread(blockV, 1).start();
     new PutThread(blockV, 2).start();
     //new PutThread(blockV, 3).start();
     //new PutThread(blockV, 4).start();
     new TakeThread(blockV).start();
     try { Thread.sleep(200); } catch (Exception e){}
     System.out.println();
     
     // CON thread.interrupt() podemos despertar un thread en wait y ATAJAR un interruptedException SI USAMOS un try-catch
     //..imaginemos el caso de un T1 lector de una conexión con sockets que llena una lista de datos y, otro T2 que lee datos de la lista, si cerramos el socket el T1 pudiera llamar a T2.interrupt para cancelar su espera.
     BlockingVector bV = new BlockingVector(1);
     PutThread pt1 = new PutThread(bV, 11); pt1.start();
     PutThread pt2 = new PutThread(bV, 12); pt2.start();
     pt2.interrupt();
     
     try { Thread.sleep(300); } catch (Exception e){}
     
     /*--------------------------------------- SOCKETS*/ System.out.println(sep+" SOCKETS");
     // Java provides an Api (Sockets) to communicate two programs on the network. Sockets are the endpoint in a two sided communication link between two applications.
     System.out.println("Muestra de serialización/deserialización de objeto");
     ObjetoSerializable objeto = new ObjetoSerializable(10, "texto", (byte)5, new ObjetoSerializable());
     System.out.println("OBJETO");
     System.out.println(objeto);
     
     byte[] bytes = new byte[10];
     try {
         bytes = toByteArray(objeto);
         System.out.println("length: " + bytes.length + " bytes");
         System.out.println("OBJETO SERIALIZADO (BYTES)");
         for(byte byte1 : bytes) {
             System.out.print(byte1 + " ");
         }System.out.println();
     } catch (IOException ex) {  }
     
     try {
         Object objeto2 = toObject(bytes);
         System.out.println("OBJETO DESERIALIZADO");
         System.out.println(objeto2);
     } catch (Exception ex) {  }
     
     System.out.println("\nENVIAR Y DEVOLVER UN OBJETO USANDO SOCKETS");
     Thread serverthread = new ServerSocketHandler(); serverthread.start();
     Thread clientthread = new ClientSocketHandler(); clientthread.start();
     
     try { serverthread.join(); clientthread.join(); } catch (InterruptedException ex) {}
     
     /*--------------------------------------- RMI*/ System.out.println(sep+" RMI");
     // The Java Remote Method Invocation (RMI) system allows an object running in one Java virtual machine to invoke methods on an object running in another Java virtual machine. RMI provides for remote communication between programs written in the Java programming language.
     System.out.println("EJEMPLOS DEL USO DE RMI EN LOS PROYECTOS ServerRMI y ClientRMI.");
     // Se creó la interface remota en un proyecto aparte(RMIinterfaces) para mostrar cómo se incluye en la librería de ServerRMI y de ClientRMI un .jar con la interface remota.
     // El proyecto ServerRMI incluye ejemplos de servidores distintos, como una app de mensajería o servicio bancario
     // Para ejecutar servidor y cliente debo hacer runFile en el archivo ya que hay varias clases con método main
     
     // LOS MÉTODOS DEL REMOTEOBJECT PUEDEN SER SYNCHRONIZED PARA QUE N THREADS NO ACCEDAN EN SIMULTÁNEO
     
     /* REMOTE OBJECT'S REFERENCE AS PARAMETERS OR AS RETURN VALUE OF A METHOD
      * - Si se envía un objeto que no está definido en el destino, al menos debe estarlo su interfaz remota y este objeto implementarla y
      *   extender de UnicastRemoteObject, así dinamicamente se descarga el stub de esta clase y se puede operar sobre ella. Revisar el proyecto
      *   sobre remoterefreturn, donde CuentaImpl(implementa interface Cuenta), se encuentra definida sólo en el servidor, pero cliente recibe
      *   un objeto tipo Cuenta(interface) y opera sobre la instancia de dicha CuentaImpl la cual fue creada en servidor.
      *   - Aquí entra en juego el tema de distributed garbage collector, donde RMI se encarga de revisar cada cierto tiempo (aparentemente el 
      *   mismo java.rmi.dgc.leaseValue) el estado de la referencia remota de ese objeto alojado en memoria del servidor, así no se destruye
      *   sino hasta que no quede ninguna ref remota ni local.
      *   Si el proceso que ha instanciado un objeto remoto desea saber cuándo no quedan más referencias remotas a ese objeto en el sistema, 
      *   aunque sí pueda existir alguna referencia local (por ejemplo, porque ese proceso ha incluido el objeto remoto en algún contenedor), 
      *   puede implementar la interfaz Unreferenced y será notificado, invocándose el método unreferenced de dicha interfaz, cuando ocurra esa
      *   circunstancia. Nótese que la detección y notificación de ese estado puede diferirse considerablemente (por defecto, puede retrasarse
      *   diez minutos, aunque se puede reducir ese valor cambiando la propiedad java.rmi.dgc.leaseValue, lo que puede implicar, sin embargo,
      *   mayor sobrecarga de mensajes de estado en el sistema). 
      */
     
     /* OBJETO COMO PARÁMETRO O RETORNO, PERO SIN SER REMOTE OBJECT
      * - Si se envía un objeto el cual está definido en fuente y destino, por ej. TitularCuenta.class, este debe ser Serializable ya que este, a 
      *   diferencia de los objetos como referencia remota, se envía como una copia del original desde donde se envió.
      * - La diferencia con los objetos como ref remota es que, si modifico el objeto remoto, el resultado se verá reflejado en la fuente de donde vino,
      *   pero si modifico un objeto no remoto que se envió como copia serializada, el original no verá el cambio, ya que en el cliente recide es una copia.
      */
     
     /* DESCARGA DINÁMICA DE CLASES
      * - Podría parecer a priori que en los ejemplos previos ya había descargas automáticas de información entre clientes y servidores, pero se 
      *   correspondían con transferencias de objetos, no de clases. Para poder usar en un método RMI dentro de una determinada JVM un objeto de un 
      *   cierto tipo (ya sea clase o interfaz), era necesario disponer de la definición de ese tipo (fichero class) en esa JVM.
      *   Pero entonces, ¿dónde surge la necesidad de la carga dinámica de clases y cómo se artícula?
      *   Pensemos en qué ocurriría si a un método RMI se le pasa un objeto que es de un tipo derivado del especificado como parámetro formal 
      *   (por ejemplo, el parámetro formal de un método RMI es de tipo FormaGeometrica y el parámetro real es de tipo Circulo). El proceso que 
      *   implementa ese método remoto debe disponer en su JVM de la clase derivada correspondiente; pero, gracias al mecanismo de descarga dinámica de
      *   clases, no es necesario que exista a priori en su JVM, sino que puede descargarlo dinámicamente desde la máquina que posee la definición de ese
      *   subtipo. Se podría decir que el servidor va aprendiendo a hacer nuevas cosas dinámicamente (en el ejemplo geométrico, el servidor aprende a 
      *   manejar círculos).
      * - Gracias a este mecanismo, el servidor y otros clientes, sin ninguna modificación, pueden gestionar objetos de la clase derivada sin disponer a
      *   priori de su código, de manera que cuando invoquen un MÉTODO SOBREESCRITO EN LA SUBCLASE se ejecute esa nueva versión.. SI QUERÉS EJECUTAR UN 
      *   MÉTODO PROPIO DE LA SUBCLASE, DEBERÁ ESTAR DEFINIDA LA SUBCLASE EN EL PROYECTO.
      * - Sin embargo, para que esta descarga dinámica funcione, a la hora de ejecutar el programa que reside en la JVM que incluye esa nueva clase, 
      *   debe especificarse la propiedad java.rmi.server.codebase para indicar la URL dónde está almacenada esa clase:
      *      java -Djava.rmi.server.codebase=file:$PWD/ -Djava.security.policy=cliente.permisos BankClient
      *   Y habilitar esa descarga en el programa que recibirá la nueva clase poniendo a falso para ello la propiedad java.rmi.server.useCodebaseOnly en
      *      java -Djava.rmi.server.useCodebaseOnly=false -Djava.security.policy=servidor.permisos
      */
     
     /* Cada clase Serializable que pasemos de un lado a otro necesita tener dos copias del fichero.class, una en el cliente y otra en el servidor, de forma que ambos puedan usarlo. Si pasamos una clase Remote, necesitamos dos copias del fichero_Stub.class, una en cliente y otra en el servidor.
     Si tenemos un programa que está mejorándose, del que sacaremos nuevas version y tenemos además muchos clientes, esto de la doble copia de ficheros puede ser un poco pesado. Cuando llegue el momento de actualizar, tenemos que ir tocando todos los clientes.
     Habilitando un mecanismo de rmi llamado carga dinámica de clases, podemos evitar hacer estas copias. Por medio de este mecanismo, el cliente y el servidor dicen dóne están sus clases Serializable y Remote (el resto pueden estar en otro lado). Este sitio debe ser accesible desde red. De esta forma, cuando el servidor, por ejemplo, necesite una clase del cliente porque la recibe como parámetro, rmi se encargará de descargar esa clase automáticamente del sitio que ha indicado el cliente.
     Sin embargo, esto abre las puertas a clientes perversos. Pueden inventarse una clase Serializable que borre el disco duro y enviarla al servidor.  Puesto que el código de esta clase se ejecuta en el servidor, se borrará el disco duro del servidor. Por ello, hay que configurar adecuadamente una política de seguridad. Veamos todo esto con más detalla.
     */
     
     //EN EL PROYECTO retornoremoteref SE APRECIAN LOS CASOS EXPLICADOS!.. REVISAR CLASES.
     
     /* - Supóngase que cliente posee una ref remota del objeto en servidor, y ya sabemos que servidor puede ser notificado cuando ya no existan más ref
      *   remotas en clientes a través de la interface Unreferenced, pero si servidor quiere forzar esta liberación de la referencia remota, pero sin
      *   cerrar el programa, puede usar el método UnicastRemoteObject.unexportObject(refObjRem, true), por ej después de haber esperado un tiempo.
      */
     
     // Note that all serializable classes, whether they implement the Serializable interface directly or indirectly, must declare a private static final field named serialVersionUID to guarantee serialization compatibility between versions. If no previous version of the class has been released, then the value of this field can be any long value, similar to the 227L used by Pi, as long as the value is used consistently in future versions. If a previous version of the class has been released without an explicit serialVersionUID declaration, but serialization compatibility with that version is important, then the default implicitly computed value for the previous version must be used for the value of the new version's explicit declaration. The serialver tool can be run against the previous version to determine the default computed value for it.
 }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 //---------------------------------------------------------------------------------------------
 
 public boolean isPerfect(int n) {
     return (addition(n,n/2) == n);
 }
 // suma los divisores de n, c empieza recibiendo la mitad de n ya que todo n NO es divisible por ningún nro más allá de su mitad
 private int addition(int n, int c) {
     if(c == 0) return c;
     if(n%c == 0) return c + addition(n,c-1);
     return addition(n,c-1);
 }

 /*El método de selección, como lo dice su nombre, selecciona el mínimo de la lista y lo intercambia con el primero, sin tomar en cuenta si el que
  *intercambió por el mínimo encontrado es menor a los demás que ahora se encuentran a su nuevo lado (en la posición donde estaba el mínimo). El ciclo
  *se repite tomando en cuenta toda la lista menos los valores que se vayan posicionando como menores.*/
 public static void ordenarSeleccion() {
     int v[] = {55,86,48,16,82};
     int aux;//copia de un valor
     int min;//apuntador
     int l = v.length;

     for(int i=0; i<l; i++) {
         min = i;
         for(int j = i+1 ; j<l; j++) {
             if(v[j] < v[min]) {
                 min = j;
             }
         }
         if(i != min) {
             aux = v[min];
             v[min] = v[i];
             v[i] = aux;
         }
     }
 }

 /*El método de inserción, posiciona un índice en el segundo elemento de la lista hasta el final, y en cada posición hace
  *un ciclo donde compara cada elemento a su izquierda con ese índice y corre todos los elementos hasta que encuentre uno que
  *no cumpla con la condición e inserta el índice allí.*/
 public static void ordenarInsercion() {
     int v[]={55,86,48,16,82};
     int j, index;

     for(int i=1; i <v.length; i++) {
         index = v[i];
         j = i-1;
         while(j >= 0 && v[j] > index) {
             v[j+1] = v[j];
             j--;
         }
         v[j+1] = index;
     }
 }

 /*El método burbuja evalúa los elementos de la lista desde el último hasta el primero, comparando cada uno con el que está
  *al lado y sustituyendo si es menor. Se excluyen del ciclo de comparación a los que ya se han ido posicionando al principio.*/
 public static void ordenarBurbuja() {
     int v[] = {55,86,48,16,82};
     int temp;

     for(int i = 1; i<v.length; i++) {
         for(int j = v.length-1 ; j >= i; j--) {
             if(v[j] < v[j-1]) {
                 temp = v[j];
                 v[j] = v[j-1];
                 v[j-1] = temp;
             }
         }
     }
 }
 
 //---------------------------------------------------------------------------------------------
 
 public static byte[] toByteArray(Object obj) throws IOException {
     byte[] bytes = null;
     ByteArrayOutputStream bos = null;
     ObjectOutputStream oos = null;
     try {
         bos = new ByteArrayOutputStream();
         oos = new ObjectOutputStream(bos);
         oos.writeObject(obj);
         oos.flush();
         
         bytes = bos.toByteArray();
     } finally {
         if (oos != null) { oos.close(); }
         if (bos != null) { bos.close(); }
     }
     return bytes;
 }

 public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
     Object obj = null;
     ByteArrayInputStream bis = null;
     ObjectInputStream ois = null;
     try {
         bis = new ByteArrayInputStream(bytes);
         ois = new ObjectInputStream(bis);
         obj = ois.readObject();
     } finally {
         if (bis != null) { bis.close(); }
         if (ois != null) { ois.close(); }
     }
     return obj;
 }
}