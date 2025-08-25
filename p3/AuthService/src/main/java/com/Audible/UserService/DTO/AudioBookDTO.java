package com.Audible.UserService.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioBookDTO {
    private int bookId;
    private String title;
    private String author;
    private String narrator;
    private int time;
    private double price;
    private String release_date;
    private String language;
    private double stars;
    private int ratings;
    private byte[] audioData;

}