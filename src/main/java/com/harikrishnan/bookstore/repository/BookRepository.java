package com.harikrishnan.bookstore.repository;
import com.harikrishnan.bookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.reviews")
    List<Book> findAllWithReviews ();
}
