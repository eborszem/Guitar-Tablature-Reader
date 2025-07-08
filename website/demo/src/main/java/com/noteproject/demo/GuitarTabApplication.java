package com.noteproject.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.noteproject.demo.Entity")
public class GuitarTabApplication {
	public static void main(String[] args) {
		SpringApplication.run(GuitarTabApplication.class, args);
	}
}