package com.harikrishnan.bookstore.service;

import com.harikrishnan.bookstore.entity.AuditLog;
import com.harikrishnan.bookstore.enums.OrderStatus;
import com.harikrishnan.bookstore.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditRepository auditRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(OrderStatus action, String description) {
        auditRepository.save(new AuditLog(action,description));
    }

}
