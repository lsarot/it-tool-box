
package files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;

public class Files {

    public static void main(String[] args) {
        
        
        
        //MANEJO DE ARCHIVOS (lectura de streams)
        //PARA QUE FUNCIONE EN VARIOS SISTEMAS: System.getProperty("user.home"), "file.separator"
        
    }

    
    /*
    La E/S en Java sigue el mismo modelo que en Unix: Abrir, usar, cerrar stream
    Flujos estándar:    System.in, System.out, System.err
    Dos tipos de clases de E/S:    Readers y Writers para texto (basados en tipo char);    InputStream y OutputStream para datos binarios (basados en tipo byte)
    
    - PODEMOS COMBINAR LOS STREAMS:    i.e. InputStream(lee bytes)-> InputStreamReader(los pasa a Unicode)-> BufferedReader(mayor eficiencia)-> Programa   (ejemplo lectura desde teclado) 
    - Ejemplo filter stream:    DataInputStream entrada = new DataInputStream( new FileInputStream("fichero.cat") );           entrada.readDouble();
                                DataOutputStream salida = new DataOutputStream( new FileOutputStream("fichero.txt") );         salida.writeDouble(120.15);
    - Ejemplo fichero:          BufferedReader entrada = new BufferedReader( new FileReader("prueba.txt") );                   entrada.readLine();
                                PrintWriter salida =  new PrintWriter( new BufferedWriter( new FileWriter("prueba.txt") ) );   salida.println("texto");                               
                                FileReader entrada = new FileReader( new File("entrada.txt") );                                while ( (dato = entrada.read()) != -1 )
                                FileWriter salida = new FileWriter( new File("salida.txt") );                                  salida.write(dato);
    - Clase File:       representa un fichero o directorio en el disco
    
    
    Jerarquía de flujo de bytes
        InputStream
            FileInputStream
            PipedInputStream
            ByteArrayInputStream
            SequenceInputStream
            StringBufferInputStream
            ObjectInputStream
            FilterInputStream
                DataInputStream (leer tipos primitivos de java)
                BufferedInputStream
                PushbackInputStream (algo de que hace pushback hasta xs char para que sea releído en la próxima lectura)
                LineNumberInputStream
    
        OutputStream
            FileOutputStream
            PipedOutputStream
            ByteArrayOutputStream
            ObjectOutputStream
            FilterOutputStream
                DataOutputStream
                BufferedOutputStream
                PushbackOutputStream
    
    Jerarquía de flujo de caracteres
        Reader
            InputStreamReader       //Lee bytes de un flujo InputStream (de bytes) y los convierte en caracteres Unicode
                FilerReader
            BufferedReader          //Entrada mediante búfer, mejora el rendimiento
                LineNumberReader
            CharArrayReader
            StringReader
            FilterReader
                PushbackReader
            PipedReader
    
        Writer
            OutputStreamWriter
                FileWriter
            BufferedWriter
            CharArrayWriter
            PrintWriter             //flujo de salida de caracteres
            StringWriter
            FilterWriter
            PipedWriter
            
     */
    private void leerTeclado() {
        try {
            int c;
            int contador = 0;
            while ((c = System.in.read()) != '\n') {//primero lee todo hasta que use Enter, luego itera sobre cada caracter
                contador++;
                System.out.print((char) c);
            }
            System.out.println();
            System.err.println("Contados " + contador + " bytes en total.");

            // ó
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in)); //ejemplo de combinación de streams
            String cadena = teclado.readLine();

            // ó
            Scanner sc = new Scanner(System.in);
            //sc...
        } catch (Exception e) {
        }
    }

    private void leerTextoFicheroPorLineas() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("nombre fichero"));
            String linea = reader.readLine();
            while (linea != null) {
                // ...procesar el texto de la línea
                linea = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) { // no se encontró el fichero
        } catch (IOException e) {
            /* algo fue mal al leer o cerrar el fichero*/ }
   
        // ó
        try {
            Scanner sc = new Scanner(new FileInputStream("nombre fichero"));
            //sc...
        } catch (FileNotFoundException e) {}
    }

    private void savingAnObject() { //guardando objeto (método de la tesis)
        /*try{
            FileInputStream fileI = new FileInputStream("C:\\Users\\Public\\localdataTerminalAdmin.DAT");
            try (ObjectInputStream input = new ObjectInputStream(fileI)) {
                ControlPrincipal.datalocal = (LocalData) input.readObject();
                input.close();
            }
        }catch(FileNotFoundException ex) {
           ..creo objeto a guardar
            FileOutputStream fileO = new FileOutputStream("C:\\Users\\Public\\localdataTerminalAdmin.DAT");
            try (ObjectOutputStream output = new ObjectOutputStream(fileO)) {
                output.writeObject( objeto );
                output.flush();  output.close();
            }
        }catch(IOException ex) {  System.out.println("Error leyendo el archivo. Eliminar el archivo.");
        }catch(ClassNotFoundException ex) {
            System.out.println("Error leyendo el archivo. Eliminar el archivo. (Objeto no encontrado).");
            //Logger.getLogger(ControlPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    private void fromNotInProgressInput() { //si se tiene de antemano todo el inputstream
        try {
            InputStream initialStream = new FileInputStream(new File("src/main/resources/sample.txt"));
            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);

            OutputStream outStream = new FileOutputStream(new File("src/main/resources/targetFile.tmp"));
            outStream.write(buffer);
            
            initialStream.close();
            outStream.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void fromInProgressInput() { //si el inputstream viene de a poco (como de una conexión)
        try {
            InputStream initialStream = new FileInputStream(new File("src/main/resources/sample.txt"));
            OutputStream outStream = new FileOutputStream(new File("src/main/resources/targetFile.tmp"));

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = initialStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            initialStream.close();
            outStream.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void fromInProgressInputWithJava8() { //como el anterior pero usando java8
        try {
            InputStream initialStream = new FileInputStream(new File("src/main/resources/sample.txt"));
            File targetFile = new File("src/main/resources/targetFile.tmp");

            java.nio.file.Files.copy(initialStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            initialStream.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void usingApacheCommonsIO() { //usando librería apache-commons-io, pero no sé si es para in progress or already known
        try {
            //copiando de archivo a archivo
            InputStream initialStream = FileUtils.openInputStream(new File("src/main/resources/ruta.jpg"));
            File targetFile = new File("src/main/resources/targetFile.jpg");
            FileUtils.copyInputStreamToFile(initialStream, targetFile);
            
            //ó extrayendo bytes
            byte[] data = FileUtils.readFileToByteArray(new File("src/main/resources/ruta.jpg"));
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    

    
}
