package com.harikrishnan.bookstore.service;

import com.harikrishnan.bookstore.dto.*;
import com.harikrishnan.bookstore.entity.*;
import com.harikrishnan.bookstore.exceptions.ResourceNotFoundException;
import com.harikrishnan.bookstore.repository.BookRepository;
import com.harikrishnan.bookstore.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private final ReviewRepository reviewRepository;

    @Transactional
    public BookResponseDto addBook(BookRequestDto bookRequestDto) {
            Book newBook = Book.builder()
                    .name(bookRequestDto.getName())
                    .price(bookRequestDto.getPrice())
                    .build();

            Book book = bookRepository.save(newBook);

            return BookResponseDto.builder()
                    .name(book.getName())
                    .price(book.getPrice())
                    .build();

    }

    private List<ReviewResponseDto> getAllReviews(List<Review> reviews) {
        return reviews.stream().map(review -> {
            return ReviewResponseDto.builder()
                    .id(review.getId())
                    .rating(review.getRating())
                    .title(review.getTitle())
                    .description(review.getDescription())
                    .build();
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponseDto> getBooks () {
        return bookRepository.findAllWithReviews().stream().map(book -> {
            return  BookResponseDto.builder()
                    .name(book.getName())
                    .price(book.getPrice())
                    .reviews(getAllReviews(book.getReviews()))
                    .build();
        }).toList();
    }

    @Transactional
    public ReviewResponseDto addReview (ReviewRequestDto reviewRequestDto, Long bookId) {

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", bookId));

        Review newReview =  Review.builder()
                .title(reviewRequestDto.getTitle())
                .description(reviewRequestDto.getDescription())
                .rating(reviewRequestDto.getRating())
                .book(book)
                .build();

        Review review = reviewRepository.save(newReview);

        return ReviewResponseDto.builder()
                .title(review.getTitle())
                .description(review.getDescription())
                .rating(review.getRating())
                .bookId(book.getId())
                .build();
    }

}
