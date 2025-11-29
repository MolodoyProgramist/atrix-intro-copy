package com.appsella.atrix.service;

import com.appsella.atrix.dto.request.RefundRequest;
import com.appsella.atrix.entity.Payment;
import com.appsella.atrix.entity.Refund;
import com.appsella.atrix.entity.User;
import com.appsella.atrix.exception.ResourceNotFoundException;
import com.appsella.atrix.repository.PaymentRepository;
import com.appsella.atrix.repository.RefundRepository;
import com.appsella.atrix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    @Transactional
    public Map<String, Object> requestRefund(RefundRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Find the most recent completed payment
        List<Payment> payments = paymentRepository.findByUserId(user.getId());
        Payment payment = payments.stream()
                .filter(p -> "completed".equals(p.getStatus()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No completed payment found"));

        // Check if within 14 days refund window
        LocalDateTime fourteenDaysAgo = LocalDateTime.now().minusDays(14);
        if (payment.getCompletedAt().isBefore(fourteenDaysAgo)) {
            throw new IllegalStateException(
                    "Refund requests must be made within 14 days of purchase");
        }

        // Create refund request
        Refund refund = Refund.builder()
                .payment(payment)
                .user(user)
                .amount(payment.getAmount())
                .status("pending")
                .reason(request.getReason())
                .paymentReceipt(request.getPaymentReceipt())
                .requestedAt(LocalDateTime.now())
                .build();

        refundRepository.save(refund);

        // Send notification email
        emailService.sendRefundRequest(user.getEmail(), refund);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("refundId", refund.getId());
        response.put("message", "Refund request submitted successfully");
        response.put("status", "pending");
        response.put("estimatedProcessingTime", "3-5 business days");

        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserRefunds(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Refund> refunds = refundRepository.findByUserId(user.getId());

        List<Map<String, Object>> refundData = refunds.stream()
                .map(this::mapRefundToResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("refunds", refundData);
        response.put("total", refunds.size());

        return response;
    }

    private Map<String, Object> mapRefundToResponse(Refund refund) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", refund.getId());
        data.put("amount", refund.getAmount());
        data.put("status", refund.getStatus());
        data.put("reason", refund.getReason());
        data.put("requestedAt", refund.getRequestedAt());
        data.put("processedAt", refund.getProcessedAt());
        return data;
    }
}
