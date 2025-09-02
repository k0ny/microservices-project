package com.xantrix.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//@EnableCaching //se non hai bisogno di specificare configurazioni sul caching
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
