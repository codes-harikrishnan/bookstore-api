package com.harikrishnan.bookstore.dto;

import com.harikrishnan.bookstore.entity.Book;
import lombok.Builder;

@Builder
public record PurchaseResponseDto(
    Long id,
    Integer quantity,
    Long bookId
) {
}
