package com.harikrishnan.bookstore.controller;

import com.harikrishnan.bookstore.dto.AuthRequestDto;
import com.harikrishnan.bookstore.dto.AuthResponseDto;
import com.harikrishnan.bookstore.dto.CustomerRequestDto;
import com.harikrishnan.bookstore.dto.CustomerResponseDto;
import com.harikrishnan.bookstore.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<CustomerResponseDto> registerUser (@RequestBody @Valid CustomerRequestDto customerRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(customerRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@RequestBody @Valid AuthRequestDto authRequestDto) {
        AuthResponseDto tokenDto = authService.authenticate(authRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(tokenDto);
    }

}
