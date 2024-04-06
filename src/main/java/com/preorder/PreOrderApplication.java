package com.preorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PreOrderApplication {

	public static void main(String[] args) {

		SpringApplication.run(PreOrderApplication.class, args);
	}

}
