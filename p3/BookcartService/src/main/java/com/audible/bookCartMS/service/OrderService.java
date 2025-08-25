package com.audible.bookCartMS.service;

import java.util.List;
import java.util.Optional;

import com.audible.bookCartMS.model.Order;

public interface OrderService {
	Order placeOrder(Order order);
    List<Order> getAllOrders();
    List<Order> getOrdersByUser(int userId);
    Optional<Order> getOrderById(String orderId);
}
