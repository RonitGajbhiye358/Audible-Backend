package com.audible.audiobookMS;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.audible.AudiobookService.exception.ResourceNotFoundException;
import com.audible.AudiobookService.model.AudioBookDTO;
import com.audible.AudiobookService.model.Audiobook;
import com.audible.AudiobookService.repository.AudiobookRepository;
import com.audible.AudiobookService.service.AudiobookServiceImp;

class AudiobookServiceTest {

    @InjectMocks
    private AudiobookServiceImp service;

    @Mock
    private AudiobookRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAudioBooks_ReturnsList() {
        List<AudioBookDTO> mockList = List.of(new AudioBookDTO());
        when(repository.findAllAudioBookDTO()).thenReturn(mockList);

        List<AudioBookDTO> result = service.getAllAudioBooks();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllAudioBooks_ThrowsException() {
        when(repository.findAllAudioBookDTO()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> service.getAllAudioBooks());
    }

    @Test
    void testSaveAudioBook() {
        Audiobook book = new Audiobook();
        when(repository.save(book)).thenReturn(book);

        Audiobook saved = service.saveAudioBook(book);
        assertEquals(book, saved);
    }

    @Test
    void testGetAudioBookById_ReturnsBook() {
        Audiobook book = new Audiobook();
        book.setBookId(1);
        when(repository.findById(1)).thenReturn(Optional.of(book));

        Optional<Audiobook> result = service.getAudioBookById(1);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetAudioBookById_ThrowsException() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getAudioBookById(1));
    }

    @Test
    void testDeleteAudioBook_Success() {
        when(repository.existsById(1)).thenReturn(true);
        doNothing().when(repository).deleteById(1);

        assertDoesNotThrow(() -> service.deleteAudioBook(1));
    }

    @Test
    void testDeleteAudioBook_ThrowsException() {
        when(repository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteAudioBook(1));
    }

    @Test
    void testUpdateAudioBookPrice_Success() {
        Audiobook book = new Audiobook();
        book.setPrice(20.0);
        when(repository.findById(1)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(book);

        Audiobook updated = service.updateAudioBookPrice(1, 30.0);
        assertEquals(30.0, updated.getPrice());
    }

    @Test
    void testUpdateAudioBookPrice_ThrowsException() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateAudioBookPrice(1, 30.0));
    }

    @Test
    void testGetByLanguage() {
        List<Audiobook> list = List.of(new Audiobook());
        when(repository.findAllByLanguage("English")).thenReturn(list);

        List<Audiobook> result = service.getByLanguage("English");
        assertEquals(1, result.size());
    }

    @Test
    void testGetAudioBookFromId_ReturnsDTOs() {
        Audiobook book = new Audiobook();
        book.setBookId(1);
        book.setTitle("Test");
        book.setAuthor("Author");
        book.setNarrator("Narrator");
        book.setPrice(10.0);
        when(repository.findById(1)).thenReturn(Optional.of(book));

        List<Integer> ids = List.of(1);
        ResponseEntity<List<AudioBookDTO>> response = service.getAudioBookFromId(ids);

        assertEquals(1, response.getBody().size());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetAudioBookFromId_ThrowsException() {
        when(repository.findById(1)).thenReturn(Optional.empty());
        List<Integer> ids = List.of(1);

        assertThrows(ResourceNotFoundException.class, () -> service.getAudioBookFromId(ids));
    }

    @Test
    void testGetByTitle_ReturnsBook() {
        Audiobook book = new Audiobook();
        book.setTitle("Sample");
        when(repository.findByTitle("Sample")).thenReturn(Optional.of(book));

        Optional<Audiobook> result = service.getByTitle("Sample");
        assertTrue(result.isPresent());
    }

    @Test
    void testGetByTitle_ThrowsException() {
        when(repository.findByTitle("Missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getByTitle("Missing"));
    }

    @Test
    void testUploadAudio_Success() throws IOException {
        Audiobook book = new Audiobook();
        book.setBookId(1);

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("audio/mpeg");
        when(file.getBytes()).thenReturn("fake audio".getBytes());

        when(repository.findById(1)).thenReturn(Optional.of(book));
        when(repository.save(any(Audiobook.class))).thenReturn(book);

        Audiobook result = service.uploadAudio(1, file);
        assertNotNull(result);
    }

    @Test
    void testUploadAudio_ThrowsForEmptyFile() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        Audiobook book = new Audiobook();
        when(repository.findById(1)).thenReturn(Optional.of(book));

        assertThrows(IllegalArgumentException.class, () -> service.uploadAudio(1, file));
    }

    @Test
    void testUploadAudio_ThrowsForInvalidType() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");

        Audiobook book = new Audiobook();
        when(repository.findById(1)).thenReturn(Optional.of(book));

        assertThrows(IllegalArgumentException.class, () -> service.uploadAudio(1, file));
    }
}
