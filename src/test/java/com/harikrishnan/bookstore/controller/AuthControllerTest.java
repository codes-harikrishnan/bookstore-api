package com.harikrishnan.bookstore.controller;

import com.harikrishnan.bookstore.configuration.JWTService;
import com.harikrishnan.bookstore.dto.CustomerRequestDto;
import com.harikrishnan.bookstore.dto.CustomerResponseDto;
import com.harikrishnan.bookstore.service.AuthService;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_WithValidRequest_shouldReturnNewlyCreatedUserResponse() throws Exception {
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder()
                .emailId("harikrishnan@gmail.com")
                .password("Abcd@234")
                .shortDescription("abcd")
                .contactNumber("+321234567890")
                .avatarUrlAddress("https://ada.com")
                .build();

        CustomerResponseDto customerResponseDto = CustomerResponseDto.builder()
                .emailId("harikrishnan@gmail.com")
                .shortDescription("abcd")
                .contactNumber("+321234567890")
                .avatarUrlAddress("https://ada.com")
                .build();

        when(authService.registerUser(any(CustomerRequestDto.class))).thenReturn(customerResponseDto);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.emailId").value(customerResponseDto.getEmailId()))
                .andExpect(jsonPath("$.shortDescription").value(customerResponseDto.getShortDescription()))
                .andExpect(jsonPath("$.contactNumber").value(customerResponseDto.getContactNumber()))
                .andExpect(jsonPath("$.avatarUrlAddress").value(customerResponseDto.getAvatarUrlAddress()));
    }

    @Test
    void registerCustomer_withInvalidRequest_ShouldReturnErrorResponse () throws Exception{
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder()
                .emailId("harikrishnan@gmail.com")
                .password("abcd")
                .shortDescription("abcd")
                .contactNumber("+321234567890")
                .avatarUrlAddress("https://ada.com")
                .build();

        CustomerResponseDto customerResponseDto = CustomerResponseDto.builder()
                .emailId("harikrishnan@gmail.com")
                .shortDescription("abcd")
                .contactNumber("+321234567890")
                .avatarUrlAddress("https://ada.com")
                .build();

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequestDto)))
                .andExpect(status().isBadRequest());

    }

}
