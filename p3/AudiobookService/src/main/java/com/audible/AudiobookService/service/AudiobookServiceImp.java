package com.audible.AudiobookService.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.audible.AudiobookService.exception.ResourceNotFoundException;
import com.audible.AudiobookService.model.AudioBookDTO;
import com.audible.AudiobookService.model.Audiobook;
import com.audible.AudiobookService.repository.AudiobookRepository;

@Service
public class AudiobookServiceImp implements AudiobookService {

	@Autowired
	private AudiobookRepository repository;

	/**
	 * Fetches all audiobooks as DTOs.
	 * Throws ResourceNotFoundException if no audiobooks are found.
	 */
	@Override
	public List<AudioBookDTO> getAllAudioBooks() {
		List<AudioBookDTO> book = repository.findAllAudioBookDTO();
	    if (book.isEmpty()) {
	        throw new ResourceNotFoundException("No Audiobook found");
	    }
	    return book;
	}

	/**
	 * Saves a new Audiobook entity to the repository.
	 */
	@Override
	public Audiobook saveAudioBook(Audiobook book) {
        return repository.save(book);
    }

	/**
	 * Retrieves an audiobook by its ID.
	 * Throws ResourceNotFoundException if the ID is invalid.
	 */
	@Override
	public Optional<Audiobook> getAudioBookById(int id) {
	    Optional<Audiobook> book = repository.findById(id);
	    if (book.isEmpty()) {
	        throw new ResourceNotFoundException("Audiobook with ID " + id + " not found");
	    }
	    return book;
	}

	/**
	 * Deletes an audiobook by ID if it exists.
	 * Throws ResourceNotFoundException if not found.
	 */
	@Override
	public void deleteAudioBook(int id) {
	    if (!repository.existsById(id)) {
	        throw new ResourceNotFoundException("Audiobook with ID " + id + " not found");
	    }
	    repository.deleteById(id);
	}

	/**
	 * Updates the price of an audiobook by its ID.
	 * Throws ResourceNotFoundException if the audiobook does not exist.
	 */
	@Override
	public Audiobook updateAudioBookPrice(int bookId, Double price) {
	    Optional<Audiobook> optionalBook = repository.findById(bookId);
	    if (optionalBook.isPresent()) {
	        Audiobook existingBook = optionalBook.get();
	        existingBook.setPrice(price);
	        return repository.save(existingBook);
	    } else {
	        throw new ResourceNotFoundException("Audiobook with ID " + bookId + " not found");
	    }
	}

	/**
	 * Retrieves all audiobooks matching a given language.
	 */
	public List<Audiobook> getByLanguage(String language) {
		return repository.findAllByLanguage(language);
	}
	
	/**
	 * Retrieves audiobook details (as DTOs) from a list of given audiobook IDs.
	 * Throws ResourceNotFoundException if any ID is not found.
	 */
	public ResponseEntity<List<AudioBookDTO>> getAudioBookFromId(List<Integer> audiobookIds) {
	    List<AudioBookDTO> audiobooksdto = new ArrayList<>();

	    for (Integer id : audiobookIds) {
	        Audiobook audiobook = repository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Audiobook with ID " + id + " not found"));

	        // Map Audiobook entity to DTO
	        AudioBookDTO wrapper = new AudioBookDTO();
	        wrapper.setBookId(audiobook.getBookId());
	        wrapper.setTitle(audiobook.getTitle());
	        wrapper.setAuthor(audiobook.getAuthor());
	        wrapper.setNarrator(audiobook.getNarrator());
	        wrapper.setPrice(audiobook.getPrice());
	        audiobooksdto.add(wrapper);
	    }

	    return new ResponseEntity<>(audiobooksdto, HttpStatus.OK);
	}

	/**
	 * Retrieves an audiobook by its title.
	 * Throws ResourceNotFoundException if not found.
	 */
	@Override
	public Optional<Audiobook> getByTitle(String title) {
		Optional<Audiobook> book = repository.findByTitle(title);
	    if (book.isEmpty()) {
	        throw new ResourceNotFoundException("Audiobook with title " + title + " not found");
	    }
	    return book;
	}

	/**
	 * Uploads audio data for a specific audiobook.
	 * Validates file type and presence.
	 * Throws IllegalArgumentException for invalid or empty files.
	 */
	@Override
    public Audiobook uploadAudio(int id, MultipartFile audioFile) throws IOException {
		// Retrieve the audiobook by ID or throw exception
		Audiobook Audiobook = getAudioBookById(id).get();

        if (audioFile.isEmpty()) {
            throw new IllegalArgumentException("Audio file cannot be empty");
        }

        // Validate MIME type starts with "audio"
        if (!audioFile.getContentType().startsWith("audio")) {
            throw new IllegalArgumentException("Invalid file type. Please upload an audio file.");
        }

        // Convert MultipartFile to byte[] and set it in the entity
        byte[] audioData = audioFile.getBytes();
        Audiobook.setAudioData(audioData);

        // Save the updated audiobook with audio data
        return repository.save(Audiobook);
    }
}
