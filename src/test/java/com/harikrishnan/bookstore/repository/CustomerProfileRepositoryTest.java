package com.harikrishnan.bookstore.repository;

import com.harikrishnan.bookstore.entity.Customer;
import com.harikrishnan.bookstore.entity.CustomerProfile;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;

@DataJpaTest
public class CustomerProfileRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerProfileRepository customerProfileRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void findByCustomer_WhenValidCustomerIsProvided_ShouldReturnCorrespondingCustomerProfile() {
        Customer customer = Customer.builder()
                .email("harikrishnan@gmail.com")
                .passwordHash("Encoded_password")
                .role("USER")
                .build();

        CustomerProfile customerProfile = CustomerProfile.builder()
                .customer(customer)
                .avatarUrl("abcd")
                .phone("+321234567890")
                .dateOfBirth(LocalDateTime.now())
                .bio("hello")
                .build();

        customerRepository.save(customer);
        customerProfileRepository.save(customerProfile);

        entityManager.flush();
        entityManager.clear();

        CustomerProfile customerProfileObtained = customerProfileRepository.findByCustomer(customer);
        assertThat(customerProfileObtained.getBio()).isEqualTo("hello");
        assertThat(customerProfileObtained.getPhone()).isEqualTo("+321234567890");
        assertThat(customerProfileObtained.getAvatarUrl()).isEqualTo("abcd");
        assertThat(customerProfileObtained.getDateOfBirth()).isEqualTo(customerProfile.getDateOfBirth());
    }



}
