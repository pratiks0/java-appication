package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring JPA Demo Application
 * 
 * Demonstrates:
 * - OneToOne relationship (Student <-> StudentProfile)
 * - ManyToOne relationship (Student -> Department)
 * - ManyToMany relationship (Student <-> Course)
 * 
 * End points:
 * - Web UI: http://localhost:8082/students
 * - REST API: http://localhost:8082/api/students
 * - Actuator: http://localhost:8082/actuator/health
 * - Swagger: http://localhost:8082/swagger-ui/index.html
 */
@SpringBootApplication
public class SpringJpaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringJpaDemoApplication.class, args);
    }
}
