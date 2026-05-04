package com.harikrishnan.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponseDto {
    private String emailId;

    private String shortDescription;

    private String contactNumber;

  //  private LocalDateTime dob;

    private String avatarUrlAddress;
}
