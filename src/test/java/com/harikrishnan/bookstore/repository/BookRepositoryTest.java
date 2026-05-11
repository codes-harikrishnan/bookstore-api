package com.harikrishnan.bookstore.repository;

import com.harikrishnan.bookstore.entity.Book;
import com.harikrishnan.bookstore.entity.Review;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;

import java.util.List;

@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void findAllWithReviews_shouldReturnBooksWithReviews () {
        Book book1 = bookRepository.save(Book.builder().name("ABC").price(BigDecimal.valueOf(100)).stock(5).build());
        Review review1 = reviewRepository.save(Review.builder().title("Very good book").description("Really informative").rating(5).book(book1).build());

        entityManager.flush();
        entityManager.clear();

        List<Book> books = bookRepository.findAllWithReviews();
        assertThat(books).hasSize(1);
        var book = books.get(0);
        assertThat(book.getId()).isNotNull();
        assertThat(book.getName()).isEqualTo("ABC");
        var review = book.getReviews().get(0);
        assertThat(review.getTitle()).isEqualTo("Very good book");
       assertThat(review.getDescription()).isEqualTo("Really informative");
    }
}
