package com.audible.AudiobookService.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.audible.AudiobookService.model.AudioBookDTO;
import com.audible.AudiobookService.model.Audiobook;
import com.audible.AudiobookService.service.AudiobookServiceImp;

import jakarta.validation.Valid;

@RestController // Indicates this class is a REST controller
@RequestMapping("/audiobooks") // Base URL for all endpoints in this controller
public class AudiobookController {
    
    @Autowired
    private AudiobookServiceImp service; // Injects the service layer implementation

    // ✅ GET: Fetch all audiobooks as DTOs
    @GetMapping("/all")
    public ResponseEntity<List<AudioBookDTO>> getAllAudioBooks(){
        return ResponseEntity.ok(service.getAllAudioBooks());
    }

    // ✅ GET: Fetch a single audiobook by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Audiobook>> getAudioBookById(@PathVariable int id) {
        return ResponseEntity.ok(service.getAudioBookById(id));
    }

    // ✅ POST: Add a new audiobook to the system
    @PostMapping("/add")
    public Audiobook addAudioBook(@Valid @RequestBody Audiobook book) {
        return service.saveAudioBook(book);
    }

    // ✅ DELETE: Delete an audiobook by ID
    @DeleteMapping("/delete/{id}")
    public String deleteAudioBook(@PathVariable int id) {
        service.deleteAudioBook(id);
        return "Audiobook deleted successfully!";
    }

    // ✅ GET: Search audiobooks by language
    @GetMapping("/search-by-language/{language}")
    public ResponseEntity<List<Audiobook>> searchByLanguage(@PathVariable String language){
        return ResponseEntity.ok(service.getByLanguage(language));
    }

    // ✅ GET: Search audiobook by title
    @GetMapping("/search-by-title/{title}")
    public Optional<Audiobook> searchByTitle(@PathVariable String title){
        return service.getByTitle(title);
    }

    // ✅ PUT: Update price of a specific audiobook
    @PutMapping("/update-price/{bookId}")
    public ResponseEntity<Audiobook> updateAudioBookPrice(
            @PathVariable int bookId,
            @RequestParam Double price) {
        try {
            Audiobook updatedBook = service.updateAudioBookPrice(bookId, price);
            return ResponseEntity.ok(updatedBook);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ✅ POST: Upload audio file for a given audiobook ID
    @PostMapping("/{id}/upload-audio")
    public ResponseEntity<String> uploadAudio(@PathVariable int id, @RequestParam("audioFile") MultipartFile audioFile) {
        try {
            service.uploadAudio(id, audioFile);
            return ResponseEntity.ok("Audio uploaded successfully for audiobook ID: " + id);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading audio file");
        }
    }

 // ✅ Get Audio for Audiobook
    @GetMapping("/{id}/audio")
    public ResponseEntity<byte[]> getAudio(@PathVariable int id) {
        try {
            Audiobook audiobook = service.getAudioBookById(id).get();

            // If no audio data is found
            if (audiobook.getAudioData() == null || audiobook.getAudioData().length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Audio file not found for Audiobook ID: ".getBytes());
            }

            // Return audio data as a downloadable response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audio_file_" + id + ".mp3")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(audiobook.getAudioData());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }

    // ✅ POST: Get a list of audiobook DTOs by their IDs — used by BookCart service
    @PostMapping("/list")
    public ResponseEntity<List<AudioBookDTO>> getAudioBookFromId(@RequestBody List<Integer> audiobookIds){
        System.out.println("Received IDs: " + audiobookIds);
        return service.getAudioBookFromId(audiobookIds);
    }
}
