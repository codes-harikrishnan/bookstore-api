package com.harikrishnan.bookstore.service;

import com.harikrishnan.bookstore.dto.*;
import com.harikrishnan.bookstore.entity.*;
import com.harikrishnan.bookstore.enums.OrderStatus;
import com.harikrishnan.bookstore.exceptions.ConflictException;
import com.harikrishnan.bookstore.exceptions.ResourceNotFoundException;
import com.harikrishnan.bookstore.repository.BookRepository;
import com.harikrishnan.bookstore.repository.BookSpecification;
import com.harikrishnan.bookstore.repository.PurchaseRepository;
import com.harikrishnan.bookstore.repository.ReviewRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private final ReviewRepository reviewRepository;

    private final PurchaseRepository purchaseRepository;

    private final AuditService auditService;

    @Transactional
    public BookResponseDto addBook(BookRequestDto bookRequestDto) {
            Book newBook = Book.builder()
                    .name(bookRequestDto.getName())
                    .price(bookRequestDto.getPrice())
                    .stock(bookRequestDto.getStock())
                    .build();

            Book book = bookRepository.save(newBook);

            return BookResponseDto.builder()
                    .name(book.getName())
                    .price(book.getPrice())
                    .stock(book.getStock())
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
    public Page<BookResponseDto> getBooks (Pageable pageable, String name, BigDecimal minPrice, BigDecimal maxPrice) {

        Specification<Book> spec = (root, query, cb) -> cb.conjunction();

        if(name != null && !name.isBlank()) {
            spec = spec.and(BookSpecification.nameContains(name));
        }

        if(minPrice != null ) {
            spec = spec.and(BookSpecification.minPrice(minPrice));
        }

        if(maxPrice != null) {
            spec = spec.and(BookSpecification.maxPrice(maxPrice));
        }


        Page<Book> bookPage = bookRepository.findAll(spec, pageable);

        List<Book> books = bookRepository.findWithReviews(bookPage.getContent());

        List <BookResponseDto> dtos = books.stream().map(book ->   BookResponseDto.builder()
                    .name(book.getName())
                    .price(book.getPrice())
                    .stock(book.getStock())
                    .reviews(getAllReviews(book.getReviews()))
                    .build()
        ).toList();

        return new PageImpl<>(dtos,pageable,bookPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public BookResponseDto getBook (Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book",bookId ));
        return  BookResponseDto.builder()
                .name(book.getName())
                .price(book.getPrice())
                .stock(book.getStock())
                .reviews(getAllReviews(book.getReviews()))
                .build();
    }

    @Transactional
    public PurchaseResponseDto purchase (@Valid PurchaseRequestDto purchaseRequestDto, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book",bookId ));
        if(book.getStock() < purchaseRequestDto.quantity()) {
            auditService.log(OrderStatus.PURCHASE_FAILED, "Unable to purchase Book with id " + book.getId()+ "and name " + book.getName() + " and quantity " + purchaseRequestDto.quantity()+". Reason: ResourceNotFoundException");
            throw new ConflictException("Required quantity of items are less than stocks available for book with id:" + bookId);
        }

        try {
            book.reduceStock(purchaseRequestDto.quantity());
            bookRepository.save(book);
             Purchase newPurchase = Purchase.builder()
                    .book(book)
                    .quantity(purchaseRequestDto.quantity())
                    .build();
                   Purchase purchase = purchaseRepository.save(newPurchase);

                   auditService.log(OrderStatus.PURCHASE_SUCCESS, "Book id=" + book.getId()+ "and name " + book.getName() + " and quantity " + purchaseRequestDto.quantity()+" purchase has been done successfully.");

            return PurchaseResponseDto.builder()
                .bookId(book.getId())
                .quantity(purchaseRequestDto.quantity())
                .id(purchase.getId())
                .build();
        } catch (Exception e) {
            auditService.log(OrderStatus.PURCHASE_FAILED, "Unable to purchase Book with id " + book.getId()+ "and name " + book.getName() + " and quantity " + purchaseRequestDto.quantity()+". Reason:"+ e.getMessage());
            throw new RuntimeException("Payment processing failed",e);
        }

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
