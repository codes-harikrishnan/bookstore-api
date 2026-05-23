package com.harikrishnan.bookstore.controller;

import com.harikrishnan.bookstore.dto.*;
import com.harikrishnan.bookstore.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
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

    @PostMapping("/{id}/purchase")
    public ResponseEntity<PurchaseResponseDto> purchaseBook (@Valid @RequestBody PurchaseRequestDto purchaseRequestDto, @PathVariable Long id){
        System.out.println("Purchase initiated....");
        return ResponseEntity.status(HttpStatus.OK).body(bookService.purchase(purchaseRequestDto, id));
    }

    @GetMapping
    public ResponseEntity<Page<BookResponseDto>> books (@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "name") String sortBy,
                                                        @RequestParam(defaultValue = "asc") String direction,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false)BigDecimal minPrice,
                                                        @RequestParam(required = false) BigDecimal maxPrice
                                                        ) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page,size, sort);

        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooks(pageable, name, minPrice, maxPrice));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBook (@Valid @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBook(id));
    }



}
