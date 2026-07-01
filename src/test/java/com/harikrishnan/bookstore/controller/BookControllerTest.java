package com.harikrishnan.bookstore.controller;

import com.harikrishnan.bookstore.configuration.JWTService;
import com.harikrishnan.bookstore.dto.BookRequestDto;
import com.harikrishnan.bookstore.dto.BookResponseDto;
import com.harikrishnan.bookstore.exceptions.ResourceNotFoundException;
import com.harikrishnan.bookstore.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addBook_WithValidRequest_ShouldReturnCreatedBook() throws Exception {
        BookRequestDto requestDto = BookRequestDto.builder()
                .name("Clean code")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .build();

        BookResponseDto responseDto = BookResponseDto.builder()
                .name("Clean code")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .build();

        when(bookService.addBook(any(BookRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Clean code"))
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void addBook_WithInvalidNameInRequest_shoulrReturn400 () throws Exception {
        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .name(null)
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .build();

      mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(bookRequestDto)))
              .andExpect(status().isBadRequest());
    }

    @Test
    void addBook_WithInvalidPriceInRequest_shouldReturn400 () throws Exception {
        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .name("Clean coding")
                .price(null)
                .stock(10)
                .build();

        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBook_WithInvalidStockInRequest_shouldReturn400 () throws Exception {
        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .name("Clean coding")
                .price(BigDecimal.valueOf(100))
                .stock(null)
                .build();
        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBook_WhenAllBookExists_ShouldReturn200 () throws  Exception {
        mockMvc.perform(get("/books").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void getBook_WhenABookExists_shouldReturn200 () throws Exception {
        BookResponseDto bookResponseDto = BookResponseDto.builder()
                .name("Clean coding")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .build();
        when(bookService.getBook(1L)).thenReturn(bookResponseDto);

        mockMvc.perform(get("/books/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void  getBook_WhenABookDoesNotExist_shouldReturn404 () throws  Exception {
        when(bookService.getBook(1L)).thenThrow(new ResourceNotFoundException("Book not found with id 1"));
        mockMvc.perform(get("/books/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

}