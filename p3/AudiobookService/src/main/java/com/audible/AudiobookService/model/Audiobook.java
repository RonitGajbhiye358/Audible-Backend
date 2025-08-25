package com.audible.AudiobookService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audiobook")
public class Audiobook {
		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int bookId;

	    @NotBlank(message = "Title is mandatory")
	    private String title;

	    @NotBlank(message = "Author is mandatory")
	    private String author;

	    @NotBlank(message = "Narrator is mandatory")
	    private String narrator;

	    @Min(value = 1, message = "Time must be at least 1 minute")
	    private int time;

	    @NotBlank(message = "Release date is required")
	    private String release_date;

	    @NotBlank(message = "Language is required")
	    private String language;

	    @DecimalMin(value = "0.0", message = "Stars cannot be negative")
	    @DecimalMax(value = "5.0", message = "Stars cannot exceed 5.0")
	    private double stars;

	    @DecimalMin(value = "0.0", message = "Price must be positive")
	    private double price;

	    @Min(value = 0, message = "Ratings cannot be negative")
	    private int ratings;

	    @Lob
	    @Column(name = "audio_data", columnDefinition = "LONGBLOB")
	    private byte[] audioData;
	}



    
    
