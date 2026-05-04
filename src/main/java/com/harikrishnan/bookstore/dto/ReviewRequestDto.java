package com.harikrishnan.bookstore.dto;

import com.harikrishnan.bookstore.entity.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ReviewRequestDto {

    @NotNull(message = "rating cannot be null")
    @Max(value = 5, message = "rating should not be more than 5")
    @Min(value = 1, message = "rating should not be less than 1")
    Integer rating;

    @NotBlank(message = "title should not be blank")
    String title;

    @NotBlank(message = "description should not be blank")
    String description;
}