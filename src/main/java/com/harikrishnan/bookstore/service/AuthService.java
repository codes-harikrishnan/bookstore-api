package com.harikrishnan.bookstore.service;

import com.harikrishnan.bookstore.configuration.JWTService;
import com.harikrishnan.bookstore.dto.AuthRequestDto;
import com.harikrishnan.bookstore.dto.AuthResponseDto;
import com.harikrishnan.bookstore.dto.CustomerRequestDto;
import com.harikrishnan.bookstore.dto.CustomerResponseDto;
import com.harikrishnan.bookstore.entity.Customer;
import com.harikrishnan.bookstore.entity.CustomerProfile;
import com.harikrishnan.bookstore.exceptions.ConflictException;
import com.harikrishnan.bookstore.repository.CustomerProfileRepository;
import com.harikrishnan.bookstore.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;

    private final CustomerProfileRepository customerProfileRepository;

    private final JWTService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public CustomerResponseDto registerUser (CustomerRequestDto customerRequestDto){
        if(customerRepository.existsByEmail(customerRequestDto.getEmailId())) {
            throw new ConflictException("A user is already registered with email id"+ customerRequestDto.getEmailId());
        }

        Customer newCustomer = Customer.builder()
                .email(customerRequestDto.getEmailId())
                .passwordHash(passwordEncoder.encode(customerRequestDto.getPassword()))
                .role("USER")
                .build();

        Customer customer =  customerRepository.save(newCustomer);

        CustomerProfile newCustomerProfile =  CustomerProfile.builder()
                .bio(customerRequestDto.getShortDescription())
                .phone(customerRequestDto.getContactNumber())
                .avatarUrl(customerRequestDto.getAvatarUrlAddress())
                .customer(customer)
                .build();

        CustomerProfile customerProfile = customerProfileRepository.save(newCustomerProfile);

        return CustomerResponseDto.builder()
                .emailId(customer.getEmail())
                .shortDescription(customerProfile.getBio())
                .contactNumber(customerProfile.getPhone())
                .avatarUrlAddress(customerProfile.getAvatarUrl())
                .build();
    }

    public AuthResponseDto authenticate (AuthRequestDto authRequestDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(),authRequestDto.getPassword()));
        return AuthResponseDto.builder()
                .token(jwtService.generateAccessToken(authRequestDto.getUsername()))
                .build();
    }
}
