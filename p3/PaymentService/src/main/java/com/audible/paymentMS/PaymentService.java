package com.audible.paymentMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class PaymentService {

	public static void main(String[] args) {
		SpringApplication.run(PaymentService.class, args);
	}

}
