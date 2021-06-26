package com.acme.basic;

import java.util.logging.Logger;

public class HelloWorld {

  private static final Logger logger = Logger.getLogger(HelloWorld.class.getName());

  void sayHello() {
    logger.info("Hello World!");
    //System.out.println("Hello World!");
  }

  void notCovered() {
    System.out.println("This method is not covered by unit tests");
  }

}
