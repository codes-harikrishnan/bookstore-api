package com.harikrishnan.bookstore.repository;

import com.harikrishnan.bookstore.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>{
    boolean existsByEmail(String email);

   Optional<Customer> findCustomerByEmail(String email);

}
