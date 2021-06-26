package com.example.codigosbasicos.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecuteOnTerminalGettingConsoleOutput {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		new ExecuteOnTerminalGettingConsoleOutput().showNumberOfProcessorsRetrievedFromTerminal();
	}

	
	public void showNumberOfProcessorsRetrievedFromTerminal() throws IOException, InterruptedException {
		System.out.println("CPUs: " + Runtime.getRuntime().availableProcessors());
		getPhysicalNumberOfCores();
	}
	
	
	private void getPhysicalNumberOfCores() throws IOException, InterruptedException
    {
		//ALGUNOS COMANDOS NO RETORNAN NADA O NO TIENEN NINGÚN EFECTO, COMO cd,
        //por eso debo usar la ruta absoluta al fichero que hace la llamada, la relativa creo q empieza en la raíz del usuario (/Users/Leo/)
        //Tampoco servirán comandos interactivos como i.e. ssh, ya que éste requiere luego una respuesta del usuario y entre una llamada a .exec y otra no hay relación.
        //Hay soluciones específicas para algunas tareas como SSH, vía otras clases.
        
    	
		//Recordar que el comando se va a ejecutar en un proceso Java aparte.
		//Otra cosa a tener en cuenta es el retorno que brindan estos métodos. Para ello tenemos el objeto de tipo Process.
		//La clase Process posee algunos métodos interesantes, en especial el metodo public abstract InputStream getInputStream(), ya que con él podemos obtener un Stream para poder leer lo que el comando que ejecutamos retornó en la consola.

		//Si es una sóla palabra, se envía sóla como String(no un vector)
		//Para enviar parámetros se debe colocar cada 'palabra' en un índice distinto en el arreglo Java:
    	
		
		// OPCION 1:
		//Process process = Runtime.getRuntime().exec(new String[]{"open","-t","/Users/Leo/Documents/NetBeansProjects/testappsencilla/src/testappsencilla/documento.txt"});

    	// OPCION 2:
        //ProcessBuilder processBuilder = new ProcessBuilder("wmic", "CPU", "Get", "NumberOfCores");//en windows
		ProcessBuilder processBuilder = new ProcessBuilder("sysctl", "hw.physicalcpu", "hw.logicalcpu");//devuelve esas 2 propiedades del sistema
		processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        
        String processOutput = getProcessOutput(process);
        
        String[] lines = processOutput.split(System.lineSeparator());
        for (String ln : lines) {
			System.out.println(ln);
		}
        
        
        // OPCION 3:
        File script = new File("/Users/Leo/Desktop/Captura de Pantalla 2021-01-24 a la(s) 8.08.08 p. m..png");
        boolean executable = script.setExecutable(true, false);
        boolean readable = script.setReadable(true, false);
        boolean writable = script.setWritable(true, false);
        
        //org.apache.commons.exec
        //CommandLine cmdLine = CommandLine.parse("...");
        //DefaultExecutor executor = new DefaultExecutor();
        //executor.setExitValue(0);
        //PumpStreamHandler streamHandler = new PumpStreamHandler(null, null, null);
        //executor.setStreamHandler(streamHandler);
        //executor.execute(cmdLine);
    }

	
    private String getProcessOutput(Process process) throws IOException, InterruptedException 
    {
        StringBuilder processOutput = new StringBuilder();

        try (BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String readLine;

            while ((readLine = processOutputReader.readLine()) != null) {
                processOutput.append(readLine);
                processOutput.append(System.lineSeparator());
            }

            int exitCode = process.waitFor();// 0 is normal termination
            System.out.println("exitCode: " + exitCode);
        }

        return processOutput.toString().trim();
    }
    
	
}
