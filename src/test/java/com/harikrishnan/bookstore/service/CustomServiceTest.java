package com.harikrishnan.bookstore.service;

import com.harikrishnan.bookstore.dto.CustomerRequestDto;
import com.harikrishnan.bookstore.dto.CustomerResponseDto;
import com.harikrishnan.bookstore.entity.Book;
import com.harikrishnan.bookstore.entity.Customer;
import com.harikrishnan.bookstore.entity.CustomerProfile;
import com.harikrishnan.bookstore.exceptions.ConflictException;
import com.harikrishnan.bookstore.exceptions.ResourceNotFoundException;
import com.harikrishnan.bookstore.repository.CustomerProfileRepository;
import com.harikrishnan.bookstore.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CustomServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerProfileRepository customerProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;


    @Test
    void saveCustomer_withValidRequest_shouldReturnCustomerResponseDto () {
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder()
                .emailId("harikrishnan@gmail.com")
                .avatarUrlAddress("https://harikrishnan.com")
                .contactNumber("+32123456789")
                .shortDescription("Hello")
                .password("abcd")
                .build();

        when(passwordEncoder.encode(any(String.class))).thenReturn("ABCD");

        Customer customer = Customer.builder()
                .email("harikrishnan@gmail.com")
                .passwordHash("ABCD")
                .build();

        CustomerProfile customerProfile = CustomerProfile.builder()
                .bio("Hello")
                .phone("+32123456789")
                .avatarUrl("https://harikrishnan.com")
                .customer(customer)
                .build();

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerProfileRepository.save(any(CustomerProfile.class))).thenReturn(customerProfile);

        CustomerResponseDto customerResponseDto =  customerService.saveCustomer(customerRequestDto);
        assertThat(customerResponseDto.getEmailId()).isEqualTo("harikrishnan@gmail.com");
        assertThat(customerResponseDto.getContactNumber()).isEqualTo("+32123456789");
        assertThat(customerResponseDto.getAvatarUrlAddress()).isEqualTo("https://harikrishnan.com");
        verify(customerRepository).save(any(Customer.class));
        verify(customerProfileRepository).save(any(CustomerProfile.class));
    }

    @Test
    void saveCustomer_onEmailExists_shouldThrowConflictException () {
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder()
                .emailId("harikrishnan@gmail.com")
                .avatarUrlAddress("https://harikrishnan.com")
                .contactNumber("+32123456789")
                .shortDescription("Hello")
                .password("abcd")
                .build();

        when(customerRepository.existsByEmail(any(String.class))).thenReturn(true);
        assertThatThrownBy(() -> customerService.saveCustomer(customerRequestDto)).isInstanceOf(ConflictException.class);
    }

    @Test
    void saveCustomer_shouldEncodePasswordBeforeSaving() {
        CustomerRequestDto customerRequestDto = CustomerRequestDto.builder()
                .emailId("harikrishnan@gmail.com")
                .password("abcd")
                .build();

        when(customerRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("abcd")).thenReturn("encoded_hash");
        when(customerRepository.save(any())).thenReturn(Customer.builder().email("harikrishnan@gmail.com").passwordHash("encoded_hash").build());
        when(customerProfileRepository.save(any())).thenReturn(CustomerProfile.builder().build());

       customerService.saveCustomer(customerRequestDto);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("encoded_hash");
        assertThat(captor.getValue().getPasswordHash()).isNotEqualTo("abcd");
    }

    @Test
    void getCustomerDetails_WhenCustomerExists_ShouldReturnResponse () {
        Customer customer = Customer.builder()
                .email("harikrishnan@gmail.com")
                .build();

        CustomerProfile customerProfile = CustomerProfile.builder()
                .bio("Hello")
                .phone("+32123456789")
                .avatarUrl("https://harikrishnan.com")
                .customer(customer)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerProfileRepository.findByCustomer(customer)).thenReturn(customerProfile);

        CustomerResponseDto customerResponseDto = customerService.getCustomerDetails(1L);
        assertThat(customerResponseDto.getEmailId()).isEqualTo("harikrishnan@gmail.com");
        assertThat(customerResponseDto.getShortDescription()).isEqualTo("Hello");
        assertThat(customerResponseDto.getAvatarUrlAddress()).isEqualTo("https://harikrishnan.com");
        assertThat(customerResponseDto.getContactNumber()).isEqualTo("+32123456789");

    }

    @Test
    void getCustomerDetails_WhenCustomerNotFound_ShouldThrowResourceNotFoundException() {
        when(customerRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> customerService.getCustomerDetails(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

}
