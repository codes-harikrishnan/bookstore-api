package com.harikrishnan.bookstore.repository;

import com.harikrishnan.bookstore.entity.Book;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class BookSpecification {

    public static Specification<Book> nameContains (String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%"+ name.toLowerCase() + "%");
    }

    public static Specification<Book> minPrice (BigDecimal minPrice) {
        return (root,query,criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"),minPrice);
    }

    public static Specification<Book> maxPrice (BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Book> inStock() {
        return (root,query,criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("stock"),0);
    }

}
