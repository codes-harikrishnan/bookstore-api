package com.harikrishnan.bookstore.repository;

import com.harikrishnan.bookstore.entity.Customer;
import com.harikrishnan.bookstore.entity.CustomerProfile;
import com.harikrishnan.bookstore.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long>, JpaSpecificationExecutor<CustomerProfile> {
    CustomerProfile findByCustomer(Customer customer);

    @EntityGraph(attributePaths = {"customer"})
    Page<CustomerProfile>findAll(Pageable pageable);


// Known: when email filter is active, Hibernate generates
// two LEFT JOINs to customers — one from Specification,
// one from @EntityGraph. Results are correct.
// TODO: refactor to QueryDSL in Phase 4 to eliminate
// the redundant join.
    @EntityGraph(attributePaths = {"customer"})
    Page<CustomerProfile>findAll(Specification<CustomerProfile> specification, Pageable pageable);
}
