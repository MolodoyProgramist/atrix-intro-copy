package com.appsella.atrix.repository;

import com.appsella.atrix.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserId(Long userId);
    List<Subscription> findByStatus(String status);
    Optional<Subscription> findByUserIdAndStatus(Long userId, String status);
    List<Subscription> findByNextBillingDateBeforeAndStatusAndAutoRenewTrue(
            LocalDateTime date, String status);
    Optional<Subscription> findByExternalSubscriptionId(String externalId);
    long countByStatus(String status);
}
