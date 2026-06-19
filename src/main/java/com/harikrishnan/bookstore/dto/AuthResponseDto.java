package com.harikrishnan.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class AuthResponseDto {
    private String token;
}
