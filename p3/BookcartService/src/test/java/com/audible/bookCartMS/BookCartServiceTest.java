package com.audible.bookCartMS;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.audible.bookCartMS.dto.AudioBookDTO;
import com.audible.bookCartMS.exception.ResourceNotFoundException;
import com.audible.bookCartMS.feign.AudiobookClient;
import com.audible.bookCartMS.model.BookCart;
import com.audible.bookCartMS.repository.BookCartRepository;
import com.audible.bookCartMS.service.BookCartServiceImp;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;

class BookCartServiceTest {

    @Mock
    private BookCartRepository bookCartRepository;

    @Mock
    private AudiobookClient audiobookClient;

    @InjectMocks
    private BookCartServiceImp bookCartService;

    private BookCart cart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cart = new BookCart();
        cart.setUserId(1);
        cart.setAudiobookIds(new ArrayList<>(List.of(101, 102)));
    }

    @Test
    void addBookCart_ShouldMergeAndSave() {
        when(bookCartRepository.findByUserId(1)).thenReturn(Optional.of(cart));
        when(bookCartRepository.save(any(BookCart.class))).thenReturn(cart);

        List<Integer> newBooks = List.of(102, 103);
        BookCart result = bookCartService.addBookCart(1, newBooks);

        assertTrue(result.getAudiobookIds().contains(101));
        assertTrue(result.getAudiobookIds().contains(103));
        verify(bookCartRepository).save(any(BookCart.class));
    }

    @Test
    void addBookCart_ShouldCreateNewCart() {
        when(bookCartRepository.findByUserId(2)).thenReturn(Optional.empty());
        when(bookCartRepository.save(any(BookCart.class))).thenAnswer(i -> i.getArgument(0));

        List<Integer> newBooks = List.of(201, 202);
        BookCart result = bookCartService.addBookCart(2, newBooks);

        assertEquals(2, result.getAudiobookIds().size());
        assertEquals(2, result.getUserId());
    }

    @Test
    void addBookCart_ShouldThrow_WhenInputInvalid() {
        assertThrows(IllegalArgumentException.class, () -> bookCartService.addBookCart(1, null));
        assertThrows(IllegalArgumentException.class, () -> bookCartService.addBookCart(1, new ArrayList<>()));
    }

    @Test
    void removeAudiobook_ShouldRemoveAndSave() {
        when(bookCartRepository.findByUserId(1)).thenReturn(Optional.of(cart));
        when(bookCartRepository.save(any(BookCart.class))).thenReturn(cart);

        BookCart result = bookCartService.removeAudiobook(1, 102);

        assertFalse(result.getAudiobookIds().contains(102));
        verify(bookCartRepository).save(any(BookCart.class));
    }

    @Test
    void removeAudiobook_ShouldThrow_IfNotInCart() {
        when(bookCartRepository.findByUserId(1)).thenReturn(Optional.of(cart));
        assertThrows(ResourceNotFoundException.class, () -> bookCartService.removeAudiobook(1, 999));
    }

    @Test
    void removeAudiobook_ShouldThrow_IfCartNotFound() {
        when(bookCartRepository.findByUserId(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookCartService.removeAudiobook(99, 101));
    }

    @Test
    void clearCart_ShouldEmptyCart() {
        when(bookCartRepository.findByUserId(1)).thenReturn(Optional.of(cart));
        when(bookCartRepository.save(any(BookCart.class))).thenReturn(cart);

        bookCartService.clearCart(1);

        assertTrue(cart.getAudiobookIds().isEmpty());
        verify(bookCartRepository).save(cart);
    }

    @Test
    void clearCart_ShouldThrow_IfNotFound() {
        when(bookCartRepository.findByUserId(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookCartService.clearCart(1));
    }

    @Test
    void getCartByUserId_ShouldReturnDTOList() {
        when(bookCartRepository.findByUserId(1)).thenReturn(Optional.of(cart));

        List<AudioBookDTO> mockDTOList = List.of(new AudioBookDTO(), new AudioBookDTO());
        when(audiobookClient.getAudiobooksByIds(anyList())).thenReturn(ResponseEntity.ok(mockDTOList));

        ResponseEntity<List<AudioBookDTO>> response = bookCartService.getCartByUserId(1);

        assertEquals(2, response.getBody().size());
        verify(audiobookClient).getAudiobooksByIds(cart.getAudiobookIds());
    }

    @Test
    void getCartByUserId_ShouldThrow_IfEmpty() {
        cart.setAudiobookIds(new ArrayList<>());
        when(bookCartRepository.findByUserId(1)).thenReturn(Optional.of(cart));
        assertThrows(ResourceNotFoundException.class, () -> bookCartService.getCartByUserId(1));
    }

    @Test
    void getCartByUserId_ShouldThrow_OnFeignNotFound() {
        when(bookCartRepository.findByUserId(1)).thenReturn(Optional.of(cart));
        when(audiobookClient.getAudiobooksByIds(anyList()))
                .thenThrow(new FeignException.NotFound("Not Found", Request.create(Request.HttpMethod.GET, "/", new HashMap<>(), null, new RequestTemplate()), null, null));

        assertThrows(ResourceNotFoundException.class, () -> bookCartService.getCartByUserId(1));
    }

    @Test
    void getAllCarts_ShouldReturnList() {
        when(bookCartRepository.findAll()).thenReturn(List.of(cart));
        List<BookCart> result = bookCartService.getAllCarts();
        assertEquals(1, result.size());
    }

    @Test
    void getCartByUserIdRaw_ShouldReturnCart() {
        when(bookCartRepository.findByUserId(1)).thenReturn(Optional.of(cart));
        BookCart result = bookCartService.getCartByUserIdRaw(1);
        assertEquals(cart, result);
    }

    @Test
    void getCartByUserIdRaw_ShouldThrow_IfNotFound() {
        when(bookCartRepository.findByUserId(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookCartService.getCartByUserIdRaw(1));
    }
}
