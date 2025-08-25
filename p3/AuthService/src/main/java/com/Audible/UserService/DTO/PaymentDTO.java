package com.Audible.UserService.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
	private int paymentId;
	private String orderId;
    private int userId;
    private double amount;
    private String paymentMode;   // e.g., UPI, CARD
    private String paymentStatus; // e.g., SUCCESS, FAILED
    private LocalDateTime timestamp;
}
