package com.audible.paymentMS.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.audible.paymentMS.exception.ResourceNotFoundException;
import com.audible.paymentMS.model.Payment;
import com.audible.paymentMS.repository.PaymentRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

    // Injecting the PaymentRepository to perform database operations related to payments
	@Autowired
	private PaymentRepository paymentRepository;

	// Processes a payment by simulating logic, updating status and timestamp, then saving it to the database
	@Override
    public String processPayment(Payment paymentRequest) {
        if (paymentRequest == null || paymentRequest.getOrderId() == null) {
            throw new IllegalArgumentException("Invalid payment request: payment or order ID is null");
        } 
        try {
            // Simulate payment processing logic
            paymentRequest.setPaymentStatus("SUCCESS");
            paymentRequest.setTimestamp(LocalDateTime.now());

            paymentRepository.save(paymentRequest);
            return "Payment successful for Order ID: " + paymentRequest.getOrderId();

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while processing payment", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during payment processing", e);
        }
    }

    // Retrieves all payment records from the database; throws exception if none found
    @Override
    public List<Payment> getAllPayments() {
        try {
            List<Payment> payments = paymentRepository.findAll();
            if (payments.isEmpty()) {
                throw new ResourceNotFoundException("No payments found");
            }
            return payments;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while retrieving all payments", e);
        }
    }

    // Retrieves all payments made by a specific user based on user ID
    @Override
    public List<Payment> getPaymentsByUser(int userId) {
        try {
            List<Payment> payments = paymentRepository.findByUserId(userId);
            if (payments.isEmpty()) {
                throw new ResourceNotFoundException("No payments found for user ID: " + userId);
            }
            return payments;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while retrieving payments for user ID: " + userId, e);
        }
    }

    // Retrieves a payment record using the associated order ID
    @Override
    public Optional<Payment> getPaymentByOrderId(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID must not be null or empty");
        }

        try {
            Optional<Payment> payment = paymentRepository.findByOrderId(orderId);
            if (payment.isEmpty()) {
                throw new ResourceNotFoundException("Payment not found for order ID: " + orderId);
            }
            return payment;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while retrieving payment for order ID: " + orderId, e);
        }
    }

}
