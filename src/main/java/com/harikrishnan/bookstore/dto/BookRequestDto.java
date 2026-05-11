package com.harikrishnan.bookstore.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDto {

    @NotBlank(message = "Book name cannot be blank")
    String name;

    @NotNull(message = "Price cannot be null")
    @Min(value = 1, message = "Should be greater than or equal to 1")
    BigDecimal price;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Should be greater than or equal to 1")
    Integer stock;

}

