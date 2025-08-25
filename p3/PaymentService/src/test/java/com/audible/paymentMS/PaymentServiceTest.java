package com.audible.paymentMS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import com.audible.paymentMS.exception.ResourceNotFoundException;
import com.audible.paymentMS.model.Payment;
import com.audible.paymentMS.repository.PaymentRepository;
import com.audible.paymentMS.service.PaymentServiceImpl;

public class PaymentServiceTest {

	@Mock
	private PaymentRepository paymentRepository;

	@InjectMocks
	private PaymentServiceImpl paymentService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testProcessPayment_Success() {
		Payment payment = new Payment();
		payment.setOrderId("ORD123");
		payment.setUserId(1);
		payment.setAmount(100.0);

		when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

		String result = paymentService.processPayment(payment);

		assertEquals("Payment successful for Order ID: ORD123", result);
		assertEquals("SUCCESS", payment.getPaymentStatus());
		assertNotNull(payment.getTimestamp());
		verify(paymentRepository).save(payment);
	}

	@Test
	void testProcessPayment_NullOrderId_ThrowsException() {
		Payment payment = new Payment(); // orderId is null

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			paymentService.processPayment(payment);
		});

		assertEquals("Invalid payment request: payment or order ID is null", exception.getMessage());
	}

	@Test
	void testGetAllPayments_Success() {
		Payment payment = new Payment();
		payment.setOrderId("ORD123");

		when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));

		var result = paymentService.getAllPayments();

		assertFalse(result.isEmpty());
		verify(paymentRepository).findAll();
	}

	@Test
	void testGetAllPayments_EmptyList_ThrowsException() {
		when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			paymentService.getAllPayments();
		});

		assertEquals("No payments found", exception.getMessage());
	}

	@Test
	void testGetPaymentsByUser_Success() {
		Payment payment = new Payment();
		payment.setUserId(1);

		when(paymentRepository.findByUserId(1)).thenReturn(Arrays.asList(payment));

		var result = paymentService.getPaymentsByUser(1);

		assertEquals(1, result.size());
		verify(paymentRepository).findByUserId(1);
	}

	@Test
	void testGetPaymentsByUser_EmptyList_ThrowsException() {
		when(paymentRepository.findByUserId(1)).thenReturn(Collections.emptyList());

		assertThrows(ResourceNotFoundException.class, () -> {
			paymentService.getPaymentsByUser(1);
		});
	}

	@Test
	void testGetPaymentByOrderId_Success() {
		Payment payment = new Payment();
		payment.setOrderId("ORD123");

		when(paymentRepository.findByOrderId("ORD123")).thenReturn(Optional.of(payment));

		Optional<Payment> result = paymentService.getPaymentByOrderId("ORD123");

		assertTrue(result.isPresent());
		assertEquals("ORD123", result.get().getOrderId());
	}

	@Test
	void testGetPaymentByOrderId_NotFound() {
		when(paymentRepository.findByOrderId("ORD999")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			paymentService.getPaymentByOrderId("ORD999");
		});
	}

	@Test
	void testGetPaymentByOrderId_NullId_ThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			paymentService.getPaymentByOrderId(null);
		});
	}

	@Test
	void testProcessPayment_DataAccessException() {
		Payment payment = new Payment();
		payment.setOrderId("ORD500");

		when(paymentRepository.save(any(Payment.class))).thenThrow(new DataAccessException("DB error") {
		});

		assertThrows(RuntimeException.class, () -> {
			paymentService.processPayment(payment);
		});
	}
}
