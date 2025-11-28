package com.chico.chico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class  ChicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChicoApplication.class, args);
	}

}
