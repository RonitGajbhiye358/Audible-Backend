package com.audible.bookCartMS.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.audible.bookCartMS.dto.PaymentRequest;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {

    @PostMapping("payments/pay")
    ResponseEntity<String> makePayment(@RequestBody PaymentRequest paymentRequest);
}

