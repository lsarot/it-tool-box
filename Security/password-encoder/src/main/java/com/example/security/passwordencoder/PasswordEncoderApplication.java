package com.example.security.passwordencoder;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

public class PasswordEncoderApplication {

	public static void main(String[] args) {
		Map<String,PasswordEncoder> encoders = new HashMap<>();
        String idForEncode = "bcrypt";
        encoders.put(idForEncode, new BCryptPasswordEncoder(12));
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder()); //This algorithm is a good choice when FIPS certification is required.
        encoders.put("scrypt", new SCryptPasswordEncoder());
        PasswordEncoder encoder = new DelegatingPasswordEncoder(idForEncode, encoders); //first is used for encoding new psw
        //PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();		
		
        System.out.print(encoder.encode(args[0]));
		//System.out.println(BCrypt.hashpw("hola", BCrypt.gensalt(12)));
	}

}

/* HOW TO USE ?
 * NOT RECOMMENDED, IT CREATES A JVM PROCESS EACH TIME IS EXECUTED, CONSUMING A LOT OF MEMORY TO JUST CREATE A HASHED PSW
 * It should be a process programmed in C or something realy fast. 


	private void executeExternalProcess() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "/Users/Leo/Documents/Software Projects/IDEs/EclipseProjects/Security/password-encoder/target/password-encoder-0.0.1-SNAPSHOT.jar", "myPassword");
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        String processOutput = getProcessOutput(process);

        String[] lines = processOutput.split(System.lineSeparator());
        for (String ln : lines) {
            System.out.println(ln);
        }
    }

    private String getProcessOutput(Process process) throws IOException, InterruptedException {
        StringBuilder processOutput = new StringBuilder();
        try (BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String readLine;
            while ((readLine = processOutputReader.readLine()) != null) {
                processOutput.append(readLine);
                processOutput.append(System.lineSeparator());
            }
            int exitCode = process.waitFor();// 0 is normal termination
            //System.out.println("exitCode: " + exitCode);
        }
        return processOutput.toString().trim();
    }
 */
