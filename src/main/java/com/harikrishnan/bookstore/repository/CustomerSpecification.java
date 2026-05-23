package com.harikrishnan.bookstore.repository;

import com.harikrishnan.bookstore.entity.Customer;
import com.harikrishnan.bookstore.entity.CustomerProfile;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecification {
    public static Specification<CustomerProfile> emailContains (String email) {
        return ((root, query, cb) -> {

            Join<CustomerProfile,Customer> join = root.join("customer", JoinType.LEFT);
          return  cb.like(cb.lower(join.get("email")),"%" + email.toLowerCase() + "%");
        });
    }
}
