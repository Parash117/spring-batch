package com.springbatch.btachprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BtachprocessingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BtachprocessingApplication.class, args);
	}
}
