package com.harikrishnan.bookstore.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(Purchase.class)
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Purchase(Integer quantity, Book book) {
        this.quantity = quantity;
        this.book = book;
    }

}
