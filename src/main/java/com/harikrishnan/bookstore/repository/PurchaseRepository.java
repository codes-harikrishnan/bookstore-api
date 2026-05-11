package com.harikrishnan.bookstore.repository;

import com.harikrishnan.bookstore.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
