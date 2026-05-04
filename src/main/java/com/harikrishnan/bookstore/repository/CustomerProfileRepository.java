package com.harikrishnan.bookstore.repository;

import com.harikrishnan.bookstore.entity.Customer;
import com.harikrishnan.bookstore.entity.CustomerProfile;
import com.harikrishnan.bookstore.exceptions.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
    CustomerProfile findByCustomer(Customer customer) throws ResourceNotFoundException;
}
