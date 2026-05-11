package com.harikrishnan.bookstore.entity;

import com.harikrishnan.bookstore.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditLog.class)
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "description", nullable = false)
    private String description;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public AuditLog(OrderStatus orderStatus, String description) {
        this.orderStatus = orderStatus;
        this.description = description;
    }
}
