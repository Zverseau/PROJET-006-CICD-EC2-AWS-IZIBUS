package com.example.izibus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IzibusApplication {

	public static void main(String[] args) {
		SpringApplication.run(IzibusApplication.class, args);
	}

}
