package com.audible.bookCartMS.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookcart")
public class BookCart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int bookCartId;
	
	private int userId;
	
	@ElementCollection
    private List<Integer> audiobookIds;
}
