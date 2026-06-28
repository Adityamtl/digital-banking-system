package com.spring_project.digital_banking_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Digital Banking System application.
 *
 * <p>A Spring Boot application providing RESTful APIs for user authentication,
 * digital wallet management, and transaction processing with JSON file-based storage.</p>
 */
@SpringBootApplication
public class DigitalBankingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalBankingSystemApplication.class, args);
	}

}
