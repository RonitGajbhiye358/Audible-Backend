package com.audible.bookCartMS.service;

import java.util.List;

import com.audible.bookCartMS.dto.AudioBookDTO;
import com.audible.bookCartMS.model.BookCart;
import com.audible.bookCartMS.model.Order;

public interface BookCartsService {
	public List<AudioBookDTO> getCartAudiobooks(int cartId);
	BookCart addBookCart(int userId, List<Integer> audiobookIds);
	Order getOrderById(String orderId);
    Order updateOrder(Order order);
}
