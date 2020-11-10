package com.c1.coins;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.c1.coins.*")
public class CoinsApplication {
	public static void main(String[] args) {
		SpringApplication.run(CoinsApplication.class, args);
	}
}
