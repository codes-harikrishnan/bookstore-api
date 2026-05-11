package com.harikrishnan.bookstore.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record PurchaseRequestDto(
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity cannot be less than one")
        Integer quantity) {
}