package com.appsella.atrix.service;

import com.appsella.atrix.entity.Payment;
import com.appsella.atrix.entity.Subscription;
import com.appsella.atrix.entity.User;
import com.appsella.atrix.exception.PaymentException;
import com.appsella.atrix.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(User user, Subscription subscription,
                                 Double amount, String paymentProvider,
                                 Map<String, Object> metadata) {

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setSubscription(subscription);
        payment.setAmount(amount);
        payment.setCurrency("USD");
        payment.setStatus("pending");
        payment.setPaymentProvider(paymentProvider);
        payment.setCreatedAt(LocalDateTime.now());

        try {
            // Сохраняем метаданные
            if (metadata != null && !metadata.isEmpty()) {
                payment.setMetadata(metadata.toString());
            }

            return paymentRepository.save(payment);

        } catch (Exception e) {
            log.error("Failed to create payment record", e);
            throw new PaymentException("Failed to create payment record");
        }
    }

    @Transactional
    public void updatePaymentStatus(String externalPaymentId, String status) {
        Payment payment = paymentRepository.findByExternalPaymentId(externalPaymentId)
                .orElseThrow(() -> new PaymentException("Payment not found"));

        payment.setStatus(status);
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        log.info("Updated payment {} status to {}", externalPaymentId, status);
    }

    @Transactional(readOnly = true)
    public Payment getPaymentByExternalId(String externalPaymentId) {
        return paymentRepository.findByExternalPaymentId(externalPaymentId)
                .orElseThrow(() -> new PaymentException("Payment not found"));
    }
}