package com.audible.bookCartMS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import com.audible.bookCartMS.exception.ResourceNotFoundException;
import com.audible.bookCartMS.model.Order;
import com.audible.bookCartMS.repository.OrderRepository;
import com.audible.bookCartMS.service.OrderServiceImpl;

class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private OrderServiceImpl orderService;

	private Order sampleOrder;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		sampleOrder = new Order();
		sampleOrder.setOrderId("order123");
		sampleOrder.setUserId(1);
//        sampleOrder.setTotalPrice(99.99);
	}

	@Test
	void placeOrder_ShouldSaveAndReturnOrder() {
		when(orderRepository.save(sampleOrder)).thenReturn(sampleOrder);

		Order result = orderService.placeOrder(sampleOrder);

		assertNotNull(result);
		assertEquals("order123", result.getOrderId());
		verify(orderRepository).save(sampleOrder);
	}

	@Test
	void placeOrder_ShouldThrow_WhenOrderIsNull() {
		assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder(null));
	}

	@Test
	void placeOrder_ShouldThrow_OnDataAccessException() {
		when(orderRepository.save(sampleOrder)).thenThrow(mock(DataAccessException.class));

		assertThrows(RuntimeException.class, () -> orderService.placeOrder(sampleOrder));
	}

	@Test
	void getAllOrders_ShouldReturnOrdersList() {
		List<Order> orders = List.of(sampleOrder);
		when(orderRepository.findAll()).thenReturn(orders);

		List<Order> result = orderService.getAllOrders();

		assertEquals(1, result.size());
		verify(orderRepository).findAll();
	}

	@Test
	void getAllOrders_ShouldThrow_WhenEmpty() {
		when(orderRepository.findAll()).thenReturn(Collections.emptyList());

		assertThrows(ResourceNotFoundException.class, () -> orderService.getAllOrders());
	}

	@Test
	void getAllOrders_ShouldThrow_OnDataAccessException() {
		when(orderRepository.findAll()).thenThrow(mock(DataAccessException.class));

		assertThrows(RuntimeException.class, () -> orderService.getAllOrders());
	}

	@Test
	void getOrdersByUser_ShouldReturnUserOrders() {
		List<Order> userOrders = List.of(sampleOrder);
		when(orderRepository.findByUserId(1)).thenReturn(userOrders);

		List<Order> result = orderService.getOrdersByUser(1);

		assertEquals(1, result.size());
		assertEquals(1, result.get(0).getUserId());
		verify(orderRepository).findByUserId(1);
	}

	@Test
	void getOrdersByUser_ShouldThrow_WhenEmpty() {
		when(orderRepository.findByUserId(2)).thenReturn(Collections.emptyList());

		assertThrows(ResourceNotFoundException.class, () -> orderService.getOrdersByUser(2));
	}

	@Test
	void getOrdersByUser_ShouldThrow_OnDataAccessException() {
		when(orderRepository.findByUserId(1)).thenThrow(mock(DataAccessException.class));

		assertThrows(RuntimeException.class, () -> orderService.getOrdersByUser(1));
	}

	@Test
	void getOrderById_ShouldReturnOrder() {
		when(orderRepository.findById("order123")).thenReturn(Optional.of(sampleOrder));

		Optional<Order> result = orderService.getOrderById("order123");

		assertTrue(result.isPresent());
		assertEquals("order123", result.get().getOrderId());
	}

	@Test
	void getOrderById_ShouldThrow_WhenNotFound() {
		when(orderRepository.findById("invalid")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById("invalid"));
	}

	@Test
	void getOrderById_ShouldThrow_WhenIdIsNullOrEmpty() {
		assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(null));
		assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById("   "));
	}

	@Test
	void getOrderById_ShouldThrow_OnDataAccessException() {
		when(orderRepository.findById("order123")).thenThrow(mock(DataAccessException.class));

		assertThrows(RuntimeException.class, () -> orderService.getOrderById("order123"));
	}
}
