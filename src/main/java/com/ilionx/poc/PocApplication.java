package com.ilionx.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The spring boot observability "leakage" POC.
 */
@SpringBootApplication
public class PocApplication {

  /**
   * The main method, to run the application with.
   */
  public static void main(final String[] args) {
    SpringApplication.run(PocApplication.class, args);
  }

}
