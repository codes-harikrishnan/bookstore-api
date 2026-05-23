package com.harikrishnan.bookstore.service;

import com.harikrishnan.bookstore.dto.CustomerRequestDto;
import com.harikrishnan.bookstore.dto.CustomerResponseDto;
import com.harikrishnan.bookstore.entity.Customer;
import com.harikrishnan.bookstore.entity.CustomerProfile;
import com.harikrishnan.bookstore.exceptions.ConflictException;
import com.harikrishnan.bookstore.exceptions.ResourceNotFoundException;
import com.harikrishnan.bookstore.repository.CustomerProfileRepository;
import com.harikrishnan.bookstore.repository.CustomerRepository;
import com.harikrishnan.bookstore.repository.CustomerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public CustomerResponseDto saveCustomer(CustomerRequestDto customerRequestDto) {

        if(customerRepository.existsByEmail(customerRequestDto.getEmailId())) {
            throw new ConflictException("A customer is already registered with the email id:"+customerRequestDto.getEmailId());
        }

        Customer newCustomer = Customer.builder()
                .email(customerRequestDto.getEmailId())
                .passwordHash(passwordEncoder.encode(customerRequestDto.getPassword())).build();
        Customer customer =  customerRepository.save(newCustomer);

   CustomerProfile newCustomerProfile =  CustomerProfile.builder()
                .bio(customerRequestDto.getShortDescription())
                //.dateOfBirth(customerRequestDto.getDob())
                .phone(customerRequestDto.getContactNumber())
                .avatarUrl(customerRequestDto.getAvatarUrlAddress())
                .customer(customer)
                .build();

   CustomerProfile customerProfile = customerProfileRepository.save(newCustomerProfile);

    return CustomerResponseDto.builder()
            .emailId(customer.getEmail())
            .shortDescription(customerProfile.getBio())
            //.dob(customerProfile.getDateOfBirth())
            .contactNumber(customerProfile.getPhone())
            .avatarUrlAddress(customerProfile.getAvatarUrl())
            .build();
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerDetails (Long id) {
            Customer customer = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer", id));

            CustomerProfile customerProfile = customerProfileRepository.findByCustomer(customer);

            return CustomerResponseDto.builder()
                    .emailId(customer.getEmail())
                    .contactNumber(customerProfile.getPhone())
                    .shortDescription(customerProfile.getBio())
                    .avatarUrlAddress(customerProfile.getAvatarUrl())
                    .build();
    }


    @Transactional(readOnly = true)
    public Page<CustomerResponseDto> getCustomers (Pageable pageable, String email) {

        org.springframework.data.jpa.domain.Specification<CustomerProfile> specification = (root, query, cb) -> cb.conjunction();

        if(email != null && !email.isBlank()) {
            specification =  specification.and(CustomerSpecification.emailContains(email));        }

        return  customerProfileRepository.findAll(specification,pageable).map(customerProfile -> CustomerResponseDto.builder()
               .contactNumber(customerProfile.getPhone())
               .shortDescription(customerProfile.getBio())
               .avatarUrlAddress(customerProfile.getAvatarUrl())
               .emailId(customerProfile.getCustomer().getEmail())
               .build());
    }

}
