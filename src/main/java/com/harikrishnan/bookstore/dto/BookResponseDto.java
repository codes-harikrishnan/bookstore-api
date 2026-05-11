package com.harikrishnan.bookstore.dto;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDto{
    String name;
    BigDecimal price;
    Integer stock;
    List<ReviewResponseDto> reviews;
}