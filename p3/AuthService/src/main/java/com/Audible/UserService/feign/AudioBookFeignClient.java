package com.Audible.UserService.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.Audible.UserService.DTO.AudioBookDTO;
import java.util.Optional;

@FeignClient("AUDIOBOOK-SERVICE")
public interface AudioBookFeignClient {

	@GetMapping("/audiobooks/all")
    List<AudioBookDTO> getAllAudioBooks();

	@GetMapping("/audiobooks/{id}")
	Optional<AudioBookDTO> getAudioBookById(@PathVariable int id);

    
    @GetMapping("/audiobooks/search-by-language/{language}")
    List<AudioBookDTO> searchByLanguage(@PathVariable String language);
    
    @PostMapping(value = "/audiobooks/{id}/upload-audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadAudio(
        @PathVariable("id") int id,
        @RequestPart("audioFile") MultipartFile file
    );

    @GetMapping(value = "/audiobooks/{id}/audio", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<byte[]> getAudio(@PathVariable("id") int id);

    @GetMapping("/audiobooks/search-by-title/{title}")
    AudioBookDTO searchByTitle(@PathVariable String title);
    
    @PostMapping("/audiobooks/add")
    AudioBookDTO addAudioBook(@RequestBody AudioBookDTO book);

    @DeleteMapping("/audiobooks/delete/{id}")
    String deleteAudioBook(@PathVariable int id);
    
    @PutMapping("/audiobooks/update-price/{bookId}")
    AudioBookDTO updateAudioBookPrice(@PathVariable int bookId, @RequestParam Double price);
    
	@PostMapping(value = "/audiobooks/list", produces = "application/json")
	public ResponseEntity<List<AudioBookDTO>> getAudiobooksByIds(@RequestBody  List<Integer> bookIds);
	
	
}
