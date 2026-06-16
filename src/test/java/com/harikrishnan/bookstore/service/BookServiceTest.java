package com.harikrishnan.bookstore.service;

import com.harikrishnan.bookstore.dto.*;
import com.harikrishnan.bookstore.entity.Book;
import com.harikrishnan.bookstore.entity.Purchase;
import com.harikrishnan.bookstore.entity.Review;
import com.harikrishnan.bookstore.enums.OrderStatus;
import com.harikrishnan.bookstore.exceptions.ConflictException;
import com.harikrishnan.bookstore.exceptions.ResourceNotFoundException;
import com.harikrishnan.bookstore.repository.BookRepository;
import com.harikrishnan.bookstore.repository.PurchaseRepository;
import com.harikrishnan.bookstore.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private BookService bookService;

    @Test
    void addBook_withValidRequest_shouldReturnBookResponseDto () {

        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .name("Clean code")
                .price(BigDecimal.valueOf(100))
                .stock(5)
                .build();

        Book savedBook = Book.builder()
                .name("Clean code")
                .price(BigDecimal.valueOf(100))
                .stock(5)
                .build();

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        BookResponseDto result = bookService.addBook(bookRequestDto);
        assertThat(result.getName()).isEqualTo("Clean code");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(result.getStock()).isEqualTo(5);
        verify(bookRepository).save(any(Book.class));

    }

    @Test
    void addBook_withBookRepositoryException_ShouldReturnErrorResponse () {
        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .name("Clean code")
                .price(BigDecimal.valueOf(100))
                .stock(5)
                .build();

        when(bookRepository.save(any())).thenThrow(new RuntimeException("Unknown error"));
        assertThatThrownBy(() -> bookService.addBook(bookRequestDto)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void getBook_WhenBookNotFound_ShouldThrowResourceNotFoundException() {
        when(bookRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBook(99L)).isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("Book");

    }

    @Test
    void getBook_WhenBookFound_ShouldReturnBookResponseDto () {
        Book book = Book.builder()
                .name("Clean code")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .build();


        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        BookResponseDto bookResponseDto = bookService.getBook(1L);
        assertThat(bookResponseDto.getName()).isEqualTo(book.getName());
        assertThat(bookResponseDto.getPrice()).isEqualTo(book.getPrice());
        assertThat(bookResponseDto.getStock()).isEqualTo(book.getStock());
        verify(bookRepository).findById(1L);
    }

    @Test
    void addReview_WithValidRequest_ShouldReturnReviewResponseDto () {
        Book book = Book.builder()
                .name("Clean code")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .build();

        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .name("Clean code")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .build();

        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                .title("New review")
                .rating(5)
                .description("New description")
                .build();

        Review review = Review.builder()
                .title("New review")
                .book(book)
                .rating(5)
                .description("New description")
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reviewRepository.save(any())).thenReturn(review);

        ReviewResponseDto reviewResponseDto = bookService.addReview(reviewRequestDto,1L);
       // assertThat(reviewResponseDto.getBookId()).isEqualTo(1L);
        assertThat(reviewResponseDto.getRating()).isEqualTo(review.getRating());
        assertThat(reviewResponseDto.getDescription()).isEqualTo(review.getDescription());
        assertThat(reviewResponseDto.getTitle()).isEqualTo(review.getTitle());
        verify(reviewRepository).save(any());
    }

    @Test
    void purchase_WhenSufficientStock_ShouldReduceStockAndAuditSuccess() {
        Book book = Book.builder()
                .name("Clean code")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .build();

        PurchaseRequestDto purchaseRequestDto = new PurchaseRequestDto(2);

        Purchase purchase = Purchase.builder()
                .quantity(2)
                .book(book)
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any())).thenReturn(book);
        when(purchaseRepository.save(any())).thenReturn(purchase);

        bookService.purchase(purchaseRequestDto,1L);

        assertThat(book.getStock()).isEqualTo(8);
        verify(auditService).log(eq(OrderStatus.PURCHASE_SUCCESS),anyString());
    }

    @Test
    void purchase_WhenInsufficientStock_ShouldThrowConflictException() {
      Book book =  Book.builder()
              .name("Clean code")
              .stock(1)
              .price(BigDecimal.valueOf(100))
              .build();

      PurchaseRequestDto purchaseRequestDto = new PurchaseRequestDto(2);

      when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

      assertThatThrownBy(() -> bookService.purchase(purchaseRequestDto,1L)).isInstanceOf(ConflictException.class);
      verify(auditService).log(eq(OrderStatus.PURCHASE_FAILED),anyString());
    }

    @Test
    void purchase_WhenExceptionThrown_ShouldLogFailureAndRethrow() {
        Book book = Book.builder()
                .name("Clean code")
                .stock(10)
                .price(BigDecimal.valueOf(100))
                .build();

        PurchaseRequestDto purchaseRequestDto = new PurchaseRequestDto(2);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(purchaseRepository.save(any())).thenThrow(new RuntimeException("DB Timeout"));

        assertThatThrownBy(() -> bookService.purchase(purchaseRequestDto, 1L)).isInstanceOf(RuntimeException.class);
        verify(auditService).log(eq(OrderStatus.PURCHASE_FAILED), anyString());

    }

    @Test
    void addReview_WhenBookNotFound_ShouldThrowResourceNotFoundException() {

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                .title("Dummy review")
                .description("Dummy description")
                .rating(5)
                .build();

        assertThatThrownBy(() -> bookService.addReview(reviewRequestDto, 1L)).isInstanceOf(ResourceNotFoundException.class);
        verify(reviewRepository, never()).save(any());
    }
}
