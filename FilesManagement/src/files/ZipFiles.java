
package files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFiles {
    
    public static void main(String[] args) {
        
        //ZipFiles app = new ZipFiles();
        //app.compressDirectoryToZipFile();
        
    }
    
    //----------------------- FICHERO ÚNICO A .ZIP
    
    /**
     * UN FICHERO A OTRO FICHERO .ZIP
     */
    private void compressOneFileToAnotherZipFile(String filePathFrom, String filePathTo, String zipEntryName) {
        byte[] buffer = new byte[1024];
        try{
            FileOutputStream fos = new FileOutputStream(filePathTo);//"/Users/Leo/Desktop/MyFile.zip"
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry(zipEntryName);//ruta.jpg
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(filePathFrom);//"/Users/Leo/Desktop/ruta.jpg"

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();

            zos.close();
            System.out.println("Done");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    //----------------------- DIRECTORIO A .ZIP

    /**
     * UN DIRECTORIO A FICHERO .ZIP
     */
    private void compressDirectoryOrFileToZip(String dirPathIn, String filePathOut) {
        File rootNode = new File(dirPathIn);
        if (rootNode.isFile()) {
            compressOneFileToAnotherZipFile(dirPathIn, filePathOut, rootNode.getName());
            return;
        }

        List<String> fileList = generateFileList(rootNode);

        zipIt(fileList, dirPathIn, filePathOut);
    }

    private List<String> generateFileList(File node) {
        List<String> fileList = new ArrayList<>();

        generateFileList(node, fileList, node.getAbsolutePath());

        return fileList;
    }

    private void generateFileList(File node, List<String> fileList, String sourceFolder) {
        //add file only
        if (node.isFile()) {
            String fullPath = node.getAbsolutePath();
            fileList.add(fullPath.substring(sourceFolder.length()+1));//se quita la ruta hasta la carpeta raíz del contenido pq no deben agregarse en las ZipEntry
        }

        if (node.isDirectory()) {
            String[] subNodes = node.list();
            for (String filename : subNodes){
                generateFileList(new File(node, filename), fileList, sourceFolder);
            }
        }
    }

    private void zipIt(List<String> fileList, String dirPathIn, String filePathOut) {

        byte[] buffer = new byte[1024];
        try{
            FileOutputStream fos = new FileOutputStream(filePathOut);
            ZipOutputStream zos = new ZipOutputStream(fos);

            zos.setLevel(Deflater.BEST_COMPRESSION);

            System.out.println("Output to Zip : " + filePathOut);
            for (String filePath : fileList) {
                System.out.println("File Added : " + filePath);

                //las entradas se agregan como si la raíz fuera el archivo .zip, es decir, no agregar /Users/Leo/xs, cualquier ruta será interna del fichero.zip
                ZipEntry ze = new ZipEntry(filePath);
                zos.putNextEntry(ze);

                //leemos el archivo del equipo
                FileInputStream in = new FileInputStream(dirPathIn + File.separator + filePath);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
            }

            zos.closeEntry();
            zos.close();

            System.out.println("Done");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    //----------------------- COMPRIMIR Y DESCOMPRIMIR BYTES
    
    /**
     * COMPRESS BYTES
     */
    public static byte[] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater();  
        deflater.setInput(data);  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        
        byte[] buffer = new byte[1024];   
        while (!deflater.finished()) {  
            int count = deflater.deflate(buffer); 
            outputStream.write(buffer, 0, count);   
        }  
        outputStream.close();  
        byte[] output = outputStream.toByteArray();  
        
        System.out.println("Original: " + data.length / 1024 + " Kb");  
        System.out.println("Comprimido: " + output.length / 1024 + " Kb");  
        return output;
    }

    /**
     * DECOMPRESS BYTES
     */
    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {  
        Inflater inflater = new Inflater();   
        inflater.setInput(data);  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
        
        byte[] buffer = new byte[1024];  
        while (!inflater.finished()) {  
            int count = inflater.inflate(buffer);  
            outputStream.write(buffer, 0, count);  
        }  
        outputStream.close();  
        byte[] output = outputStream.toByteArray();  
        
        System.out.println("Original: " + data.length / 1024 + " Kb");  
        System.out.println("Comprimido: " + output.length / 1024 + " Kb");  
        return output;
    }
    
}
