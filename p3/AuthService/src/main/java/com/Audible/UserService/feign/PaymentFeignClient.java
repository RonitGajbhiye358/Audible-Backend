package com.Audible.UserService.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.Audible.UserService.DTO.PaymentDTO;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentFeignClient {

    @PostMapping("/payments/pay")
    ResponseEntity<String> makePayment(@RequestBody PaymentDTO paymentRequest); // or PaymentDTO if you prefer DTOs

    // USER routes
    @GetMapping("/payments/user/{userId}")
    ResponseEntity<List<PaymentDTO>> getPaymentsByUser(@PathVariable("userId") int userId);

    @GetMapping("/payments/order/{orderId}")
    ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable("orderId") String orderId);

    // ADMIN route
    @GetMapping("/payments/all")
    ResponseEntity<List<PaymentDTO>> getAllPayments();
}