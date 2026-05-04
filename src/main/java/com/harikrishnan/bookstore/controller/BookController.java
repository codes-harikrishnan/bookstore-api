package com.harikrishnan.bookstore.controller;

import com.harikrishnan.bookstore.dto.*;
import com.harikrishnan.bookstore.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookResponseDto> addBook(@Valid @RequestBody BookRequestDto book){
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(book));
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewResponseDto> addReview (@Valid @RequestBody ReviewRequestDto review,@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addReview(review, id));
    }

    @GetMapping("/")
    public ResponseEntity<List<BookResponseDto>> books () {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooks());
    }
}
