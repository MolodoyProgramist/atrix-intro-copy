package com.appsella.atrix.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String planType; // "monthly" or "yearly"

    @Column(nullable = false)
    private String status; // "active", "canceled", "expired", "pending", "trial"

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime trialEndDate;
    private LocalDateTime canceledAt;
    private LocalDateTime nextBillingDate;

    private Double amount;
    private String currency = "USD";

    private String paymentProvider; // "stripe", "paypal", "card"
    private String externalSubscriptionId;

    @Column(nullable = false)
    private Boolean autoRenew = true;

    @Column(columnDefinition = "TEXT")
    private String cancellationReason;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
