package com.luchavor.batchprocess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchApplication {
	public static void main(String[] args) throws Exception {
		System.exit(SpringApplication.exit(SpringApplication.run(BatchApplication.class, args)));
	}
}