package com.audible.AudiobookService.model;

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
    
    @Override
    public String toString() {
        return "AudioBookDTO{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", narrator='" + narrator + '\'' +
                ", time=" + time +
                ", price=" + price +
                ", release_date='" + release_date + '\'' +
                ", language='" + language + '\'' +
                ", stars=" + stars +
                ", ratings=" + ratings +
                ", audioData=" + (audioData != null ? "length=" + audioData.length + " bytes" : "null") +
                '}';
    }

}