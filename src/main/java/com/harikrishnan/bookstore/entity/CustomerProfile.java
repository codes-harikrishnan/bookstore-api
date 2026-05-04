package com.harikrishnan.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "customer_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;

    private String phone;

    private LocalDateTime dateOfBirth;

    private String avatarUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", unique = true)
    private Customer customer;

    @Builder
    public CustomerProfile(String bio, String phone, LocalDateTime dateOfBirth, String avatarUrl, Customer customer ){
        this.bio = bio;
        this.phone = phone;
        this.avatarUrl= avatarUrl;
        this.customer = customer;
        this.dateOfBirth = dateOfBirth;
    }
}
