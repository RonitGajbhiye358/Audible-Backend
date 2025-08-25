package com.Audible.UserService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.Audible.UserService.DTO.AudioBookDTO;
import com.Audible.UserService.DTO.BookCartDTO;
import com.Audible.UserService.DTO.OrderDTO;
import com.Audible.UserService.DTO.PaymentDTO;
import com.Audible.UserService.entity.user;
import com.Audible.UserService.feign.AudioBookFeignClient;
import com.Audible.UserService.feign.BookCartFeignClient;
import com.Audible.UserService.feign.PaymentFeignClient;
import com.Audible.UserService.service.ServiceImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    // Injecting the service implementation for user-related operations
    @Autowired
    private ServiceImpl service;
    
    // Injecting Feign client to communicate with the payment microservice
    @Autowired
    private PaymentFeignClient paymentClient;
    
    // Injecting Feign client to communicate with the audiobook microservice
    @Autowired
    private AudioBookFeignClient audiobookClient;
    
    // Injecting Feign client to communicate with the book cart microservice
    @Autowired
    private BookCartFeignClient bookCartClient;
    
    // Retrieve all audiobooks from the audiobook service
    @GetMapping("/all-audiobooks")
    public ResponseEntity<List<AudioBookDTO>> getAllAudioBooks() {
        return ResponseEntity.ok(audiobookClient.getAllAudioBooks());
    }

    // Add a new audiobook using POST request
    @PostMapping("/audiobook/add")
    public ResponseEntity<AudioBookDTO> addAudioBook(@RequestBody AudioBookDTO book) {
    	AudioBookDTO saved = audiobookClient.addAudioBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Delete an audiobook by its ID
    @DeleteMapping("/audiobook/delete/{id}")
    public ResponseEntity<String> deleteAudioBook(@PathVariable int id) {
        String message = audiobookClient.deleteAudioBook(id);
        return ResponseEntity.ok(message);
    }
    
    // Upload audio file for a specific audiobook
    @PostMapping("/audiobook/upload-audio/{bookId}")
    public ResponseEntity<String> uploadAudioForBook(
            @PathVariable int bookId,
            @RequestParam("audioFile") MultipartFile audioFile) {
        return audiobookClient.uploadAudio(bookId, audioFile);
    } 
    
    // Update the price of an audiobook
    @PutMapping("/audiobook/update-price/{bookId}")
    public ResponseEntity<AudioBookDTO> updateAudioBookPrice(@PathVariable int bookId, @RequestParam Double price) {
    	AudioBookDTO updatedBook = audiobookClient.updateAudioBookPrice(bookId, price);
        return ResponseEntity.ok(updatedBook);
    }

    // Retrieve all users
    @GetMapping("/getAllUsers")
	public List<user> getAllUsers() {
		return service.getAllUsers();
	}
	
	// Get user by username
    @GetMapping("/user/{username}")
    public user getUserByUsername(@PathVariable String username) {
        return service.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
    
    // Get user by customer/user ID
    @GetMapping("/user/getByUserId/{customerId}")
    public Optional<user> getUserByCustomerId(@PathVariable Integer customerId) {
        return service.getUserByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("User not found with userId: " + customerId));
    }

    // Delete user by ID
    @DeleteMapping("/user/{customerId}")
    public void deleteUserById(@PathVariable Integer customerId) {
        service.deleteUserById(customerId);
    }

    // Update user role (e.g., to ADMIN or USER)
    @PutMapping("/user/update-role/{customerId}")
    public void updateAuthUserRole(@PathVariable Integer customerId, @RequestParam String role) {
        service.updateUserRole(customerId, role);
    }

    // Get total count of users
    @GetMapping("/user/count")
    public ResponseEntity<Long> getUserCount() {
        long count = service.getUserCount();
        return ResponseEntity.ok(count);
    }
    
    // Fetch all book carts from book cart microservice
    @GetMapping("/all/bookcarts")
    public List<BookCartDTO> fetchAllBookCarts() {
        return bookCartClient.getAllBookCarts();
    }
    
    // Orders

    // Fetch all orders from book cart microservice
    @GetMapping("/orders/get-all")
    public ResponseEntity<List<OrderDTO>> fetchAllOrders(){
    	return bookCartClient.getAllOrders();
    }
    
    // Payments

    // Fetch all payment records from payment microservice
    @GetMapping("/payments/get-all")
    public ResponseEntity<List<PaymentDTO>> fetchAllPayments(){
    	return paymentClient.getAllPayments();
    }
}
