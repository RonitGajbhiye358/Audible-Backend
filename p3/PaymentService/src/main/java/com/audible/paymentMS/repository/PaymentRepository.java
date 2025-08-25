package com.audible.paymentMS.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.audible.paymentMS.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
	List<Payment> findByUserId(int userId);
	Optional<Payment> findByOrderId(String orderId);
}