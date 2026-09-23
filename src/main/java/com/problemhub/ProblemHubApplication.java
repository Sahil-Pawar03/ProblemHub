package com.problemhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ProblemHub Main Application
 * Real-World Problems -> Student Projects
 */
@SpringBootApplication
public class ProblemHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProblemHubApplication.class, args);
        System.out.println("==================================================");
        System.out.println("🚀 ProblemHub Application Started Successfully!");
        System.out.println("👉 Access at: http://localhost:8080");
        System.out.println("==================================================");
    }
}
