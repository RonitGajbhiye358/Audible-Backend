package com.Audible.UserService.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.Audible.UserService.DTO.AudioBookDTO;
import com.Audible.UserService.DTO.BookCartDTO;
import com.Audible.UserService.DTO.OrderDTO;

@FeignClient("BOOKCART-SERVICE")
public interface BookCartFeignClient {
	    
		@GetMapping("/cart/all-carts")  // endpoint exposed by BookCart MS
	    List<BookCartDTO> getAllBookCarts();
		
		@PostMapping("/cart/add/{userId}")
		BookCartDTO addBookCart(@PathVariable int userId, @RequestBody List<Integer> audiobookIds);

		@DeleteMapping("/cart/remove/{userId}/{audiobookId}")
		BookCartDTO removeAudiobook(@PathVariable int userId,@PathVariable int audiobookId);

		@DeleteMapping("/cart/clear/{userId}")
		void clearCart(@PathVariable int userId);

		@GetMapping("/cart/get/{userId}")
		ResponseEntity<List<AudioBookDTO>> getCartByUserId(@PathVariable Integer userId);
		
		// ****Order endpoints
		// USER
		@GetMapping("/orders/user/{userId}")
		ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable("userId") int userId);

		@GetMapping("/orders/{orderId}")
		ResponseEntity<OrderDTO> getOrderById(@PathVariable("orderId") String orderId);

		// ADMIN
		@GetMapping("/orders/all")
		ResponseEntity<List<OrderDTO>> getAllOrders();

		// Trigger order placement + payment
		@PostMapping("/orders/place/{userId}/{mode}")
		ResponseEntity<String> placeOrder(@PathVariable("userId") int userId, @PathVariable("mode") String mode);

}
