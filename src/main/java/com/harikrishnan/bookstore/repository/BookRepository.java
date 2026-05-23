package com.harikrishnan.bookstore.repository;
import com.harikrishnan.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book,Long>, JpaSpecificationExecutor<Book> {

    Page<Book>findAll(Pageable pageable);

    @Query("SELECT DISTINCT b from Book b LEFT JOIN FETCH  b.reviews WHERE b IN :books")
    List<Book> findWithReviews(@Param("books") List<Book> books);

    @EntityGraph(attributePaths = {"reviews"})
    Optional<Book>findById (Long id);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.reviews")
    List<Book> findAllWithReviews ();
}
