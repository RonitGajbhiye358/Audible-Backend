package com.audible.paymentMS.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audible.paymentMS.model.Payment;
import com.audible.paymentMS.service.PaymentServiceImpl;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentServiceImpl paymentService;

    @PostMapping("/pay")
    public ResponseEntity<String> makePayment(@RequestBody Payment paymentRequest) {
        String response = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }
    
    //****USER
 // Get all payments for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUser(@PathVariable int userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
    }

    // Get payment by order ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable String orderId) {
        return ResponseEntity.of(paymentService.getPaymentByOrderId(orderId));
    }

    
    //****ADMIN
    @GetMapping("/all")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

}

