package com.appsella.atrix.repository;

import com.appsella.atrix.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByStatus(String status);
    Optional<Payment> findByExternalPaymentId(String externalId);
    List<Payment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
