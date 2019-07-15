package com.hanogi.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan({ "com.hanogi.batch" })
public class BatchprocessorApplication {
	private static final Logger log = LoggerFactory.getLogger(BatchprocessorApplication.class);

	public static void main(String[] args) {
		log.info("Application started.....");

		SpringApplication.run(BatchprocessorApplication.class, args);
	}

}
