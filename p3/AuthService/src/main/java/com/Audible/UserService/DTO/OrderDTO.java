package com.Audible.UserService.DTO;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
	private String orderId;
	private int userId;
	private List<Integer> audiobookIds;
	private double totalAmount;
}
