package com.audible.paymentMS.service;


import java.util.List;
import java.util.Optional;

import com.audible.paymentMS.model.Payment;

public interface PaymentService {
    String processPayment(Payment paymentRequest);
    List<Payment> getAllPayments();
    List<Payment> getPaymentsByUser(int userId);
    Optional<Payment> getPaymentByOrderId(String orderId);
}
