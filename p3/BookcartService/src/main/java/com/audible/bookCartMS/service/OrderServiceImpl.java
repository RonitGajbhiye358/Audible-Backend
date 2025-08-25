package com.audible.bookCartMS.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.audible.bookCartMS.exception.ResourceNotFoundException;
import com.audible.bookCartMS.model.Order;
import com.audible.bookCartMS.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {

	// Injecting the OrderRepository dependency for database operations related to orders
	@Autowired
	private OrderRepository orderRepository;

	// Places a new order in the system after validating and saving to the database
	@Override
	public Order placeOrder(Order order) {
		if (order == null) {
			throw new IllegalArgumentException("Order object must not be null");
		}

		try {
			return orderRepository.save(order);
		} catch (DataAccessException e) {
			throw new RuntimeException("Failed to place order due to database error", e);
		}
	}

	// Retrieves all orders from the database; throws exception if none are found
	@Override
	public List<Order> getAllOrders() {
		try {
			List<Order> orders = orderRepository.findAll();
			if (orders.isEmpty()) {
				throw new ResourceNotFoundException("No orders found");
			}
			return orders;
		} catch (DataAccessException e) {
			throw new RuntimeException("Database error while retrieving all orders", e);
		}
	}

	// Retrieves all orders associated with a specific user ID; throws exception if none are found
	@Override
	public List<Order> getOrdersByUser(int userId) {
		try {
			List<Order> orders = orderRepository.findByUserId(userId);
			if (orders.isEmpty()) {
				throw new ResourceNotFoundException("No orders found for user ID: " + userId);
			}
			return orders;
		} catch (DataAccessException e) {
			throw new RuntimeException("Database error while retrieving orders for user ID: " + userId, e);
		}
	}

	// Retrieves a specific order by its ID; throws exception if ID is invalid or order is not found
	@Override
	public Optional<Order> getOrderById(String orderId) {
		if (orderId == null || orderId.trim().isEmpty()) {
			throw new IllegalArgumentException("Order ID must not be null or empty");
		}

		try {
			Optional<Order> order = orderRepository.findById(orderId);
			if (order.isEmpty()) {
				throw new ResourceNotFoundException("Order not found with ID: " + orderId);
			}
			return order;
		} catch (DataAccessException e) {
			throw new RuntimeException("Database error while retrieving order by ID: " + orderId, e);
		}
	}

}
