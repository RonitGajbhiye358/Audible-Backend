package com.audible.bookCartMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients  // Enables Feign Clients
public class BookCartMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookCartMsApplication.class, args);
	}

}
