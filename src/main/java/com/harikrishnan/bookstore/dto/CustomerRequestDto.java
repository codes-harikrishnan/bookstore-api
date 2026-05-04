package com.harikrishnan.bookstore.dto;

import com.harikrishnan.bookstore.entity.Book;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequestDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be in correct format")
    private String emailId;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be of atleast 8 characters")
    private String password;

    private String shortDescription;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

   // private LocalDateTime dob;

    private String avatarUrlAddress;

}
