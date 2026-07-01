package com.harikrishnan.bookstore.controller;
import com.harikrishnan.bookstore.configuration.JWTService;
import com.harikrishnan.bookstore.dto.CustomerRequestDto;
import com.harikrishnan.bookstore.dto.CustomerResponseDto;
import com.harikrishnan.bookstore.exceptions.ResourceNotFoundException;
import com.harikrishnan.bookstore.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;


    @Test
    void getCustomerDetails_withValidId_ShouldReturnProperCustomerResponseDto () throws Exception {
        CustomerResponseDto customerResponseDto = CustomerResponseDto.builder()
                .emailId("harikrishnan@gmail.com")
                .shortDescription("abcd")
                .contactNumber("+321234567890")
                .avatarUrlAddress("https://ada.com")
                .build();

        when(customerService.getCustomerDetails(any())).thenReturn(customerResponseDto);

        mockMvc.perform(get("/customer/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailId").value(customerResponseDto.getEmailId()))
                .andExpect(jsonPath("$.avatarUrlAddress").value(customerResponseDto.getAvatarUrlAddress()))
                .andExpect(jsonPath("$.contactNumber").value(customerResponseDto.getContactNumber()))
                .andExpect(jsonPath("$.shortDescription").value(customerResponseDto.getShortDescription()));
    }

    @Test
    void getCustomerDetails_withNonExistingId_ShouldReturnResourceNotFoundException() throws Exception {
        when(customerService.getCustomerDetails(any())).thenThrow(new ResourceNotFoundException("Customer not found"));
        mockMvc.perform(get("/customer/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
