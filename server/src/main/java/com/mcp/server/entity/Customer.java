package com.mcp.server.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String phone;
    private String preferredLanguage;

    @Enumerated(EnumType.STRING)
    private Enums.LoyaltyTier loyaltyTier;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public Enums.LoyaltyTier getLoyaltyTier() {
        return loyaltyTier;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}