package com.Audible.UserService.DTO;

import java.util.List;

import jakarta.persistence.ElementCollection;
import lombok.Data;

@Data
public class BookCartDTO {
	private int bookCartId;
	
	private int userId;
	
    private List<Integer> audiobookIds;
}
