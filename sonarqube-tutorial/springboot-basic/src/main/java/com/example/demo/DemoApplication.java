package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.beust.jcommander.*;
import org.apache.commons.cli.*;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		//// EJEMPLOS DEL SONARQUBE PARA PURGAR LOS COMMAND LINE ARGUMENTS ////

			// === JCommander ===
			/*
			The idea behind JCommander is to have a easy configurable option to provide command line parameters , their default values and so on.
			Lets say your program needs an int parameter jdbc_batch_size with default value to be 2500 with an option to override that value .

			DemoApplication main = new DemoApplication();
		    JCommander.newBuilder()
		      .addObject(main)
		      .build()
		      .parse(args); // Sensitive
		    main.run();
			*/
		    // === Apache CLI ===
		    /*
			Options options = new Options();
		    CommandLineParser parser = new DefaultParser();
		    try {
		      CommandLine line = parser.parse(options, args); // Sensitive
		    }
		    */

		CliOptions cli = new CliOptions();
		JCommander cmdr = new JCommander(cli, args);
		int jdbc_batch_size = cli.jdbc_batch_size;
		//if you wish to override default value, you provide -batchSize 1000 via command line 

		System.out.println("batchSize is: " + jdbc_batch_size);
	}

	public static class CliOptions {
		@Parameter(names = "-batchSize", description = "JDBC batch size", required=false)
		public int jdbc_batch_size = 2500;
		@Parameter(names = { "-c", "--config" }, description = "Sets the location of the configuration file.")
		public String configFile;
	}
}
