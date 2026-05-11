package com.harikrishnan.bookstore.repository;

import com.harikrishnan.bookstore.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditLog, Long> {
}
