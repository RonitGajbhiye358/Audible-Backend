package com.Audible.UserService.controller;

import com.Audible.UserService.DTO.AudioBookDTO;
import com.Audible.UserService.DTO.BookCartDTO;
import com.Audible.UserService.DTO.OrderDTO;
import com.Audible.UserService.DTO.PaymentDTO;
import com.Audible.UserService.entity.user;
import com.Audible.UserService.feign.AudioBookFeignClient;
import com.Audible.UserService.feign.BookCartFeignClient;
import com.Audible.UserService.feign.PaymentFeignClient;
import com.Audible.UserService.service.ServiceImpl;

import java.util.Optional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    // Injecting necessary services and Feign clients
    @Autowired
    private ServiceImpl service;
    
    @Autowired
    private PaymentFeignClient paymentClient;
    
    @Autowired
    private AudioBookFeignClient audiobookClient;
    
    @Autowired
    private BookCartFeignClient bookCartClient;

    // Get all available audiobooks
    @GetMapping("/all-audiobooks")
    public ResponseEntity<List<AudioBookDTO>> getAllAudioBooks() {
        return ResponseEntity.ok(audiobookClient.getAllAudioBooks());
    }

    // Get audiobook details by its ID
    @GetMapping("/audiobooks/{id}")
    public ResponseEntity<AudioBookDTO> getAudioBookById(@PathVariable int id) {
        Optional<AudioBookDTO> optional = audiobookClient.getAudioBookById(id);
        return optional.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Fetch audio file bytes for a specific audiobook
    @GetMapping("/audiobook/audio/{bookId}")
    public ResponseEntity<byte[]> getAudioForBook(@PathVariable int bookId) {
        return audiobookClient.getAudio(bookId);
    }

    // Search audiobooks by language
    @GetMapping("/audiobooks/search-by-language/{language}")
    public ResponseEntity<List<AudioBookDTO>> searchByLanguage(@PathVariable String language) {
        return ResponseEntity.ok(audiobookClient.searchByLanguage(language));
    }

    // Search audiobook by title
    @GetMapping("/audiobooks/search-by-title/{title}")
    public ResponseEntity<AudioBookDTO> searchByTitle(@PathVariable String title) {
        return ResponseEntity.ok(audiobookClient.searchByTitle(title));
    }

    // Add audiobooks to the user's cart. Creates cart if not present
    @PostMapping("/cart/add/{userId}")
    public ResponseEntity<BookCartDTO> addToCart(
            @PathVariable int userId,
            @RequestBody List<Integer> audiobookIds) {
        BookCartDTO savedCart = bookCartClient.addBookCart(userId, audiobookIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCart);
    }

    // Retrieve list of audiobooks from user's cart
    @GetMapping("/cart/get/{userId}")
    public ResponseEntity<List<AudioBookDTO>> getUserCart(@PathVariable Integer userId) {
        return bookCartClient.getCartByUserId(userId);
    }

    // Remove a specific audiobook from the user's cart
    @DeleteMapping("/cart/remove/{userId}/{audiobookId}")
    public ResponseEntity<BookCartDTO> removeAudiobookFromCart(
            @PathVariable int userId,
            @PathVariable int audiobookId) {
        BookCartDTO updatedCart = bookCartClient.removeAudiobook(userId, audiobookId);
        return ResponseEntity.ok(updatedCart);
    }

    // Clear all audiobooks from the user's cart
    @DeleteMapping("/cart/clear/{userId}")
    public ResponseEntity<String> clearUserCart(@PathVariable int userId) {
        bookCartClient.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully for user ID: " + userId);
    }
    
    // ----------------- Orders -----------------

    // Get all orders placed by a specific user
    @GetMapping("/orders/getByUserId/{userId}")
    public ResponseEntity<List<OrderDTO>> fetchOrdersByUserId(@PathVariable("userId") int userId) {
        return bookCartClient.getOrdersByUser(userId);
    }

    // Get specific order details by order ID
    @GetMapping("/orders/getByOrderId/{orderId}")
    public ResponseEntity<OrderDTO> fetchOrderByOrderId(@PathVariable("orderId") String orderId) {
        return bookCartClient.getOrderById(orderId);
    }

    // Place an order for a user with specified payment mode
    @PostMapping("/orders/place-order/{userId}/{mode}")
    public ResponseEntity<String> InitiateOrder(@PathVariable("userId") int userId, @PathVariable("mode") String mode) {
        return bookCartClient.placeOrder(userId, mode);
    }

    // ----------------- Payments -----------------

    // Get all payment transactions for a user
    @GetMapping("/payments/getByUserId/{userId}")
    public ResponseEntity<List<PaymentDTO>> fetchPaymentsByUserId(@PathVariable("userId") int userId) {
        return paymentClient.getPaymentsByUser(userId);
    }

    // Get payment details by order ID
    @GetMapping("/payments/getByOrderId/{orderId}")
    public ResponseEntity<PaymentDTO> fetchPaymentByOrderId(@PathVariable("orderId") String orderId) {
        return paymentClient.getPaymentByOrderId(orderId);
    }
}
