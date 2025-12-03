package com.appsella.atrix.repository;

import com.appsella.atrix.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findByUserId(Long userId);
    List<Refund> findByStatus(String status);
    List<Refund> findByPaymentId(Long paymentId);
}
