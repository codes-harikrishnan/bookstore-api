package com.harikrishnan.bookstore.dto;

import com.harikrishnan.bookstore.entity.Review;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Review}
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto implements Serializable {
    Long id;
    Integer rating;
    String title;
    String description;
    Long bookId;
}