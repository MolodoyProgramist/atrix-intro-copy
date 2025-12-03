package com.appsella.atrix.service;

import com.appsella.atrix.dto.request.CancelSubscriptionRequest;
import com.appsella.atrix.dto.request.SubscriptionCreateRequest;
import com.appsella.atrix.entity.Subscription;
import com.appsella.atrix.entity.User;
import com.appsella.atrix.exception.ResourceNotFoundException;
import com.appsella.atrix.repository.SubscriptionRepository;
import com.appsella.atrix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public Map<String, Object> createSubscription(SubscriptionCreateRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Calculate amount based on plan type
        double amount = "monthly".equals(request.getPlanType()) ? 9.99 : 79.99;

        // Create subscription record (без Stripe)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime trialEnd = now.plusDays(3); // 3-day trial
        LocalDateTime nextBilling = trialEnd;

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlanType(request.getPlanType());
        subscription.setStatus("active");
        subscription.setAmount(amount);
        subscription.setCurrency("USD");
        subscription.setPaymentProvider("manual");
        subscription.setExternalSubscriptionId("manual_" + UUID.randomUUID().toString());
        subscription.setCreatedAt(now);
        subscription.setTrialEndDate(trialEnd);
        subscription.setNextBillingDate(nextBilling);
        subscription.setAutoRenew(true);

        subscriptionRepository.save(subscription);

        // Send confirmation email
        emailService.sendSubscriptionConfirmation(user.getEmail(), subscription);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("subscriptionId", subscription.getId());
        response.put("trialPeriod", "3 days");
        response.put("message", "Subscription created successfully");

        return response;
    }

    @Transactional
    public Map<String, Object> cancelSubscription(CancelSubscriptionRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Subscription subscription = subscriptionRepository
                .findByUserIdAndStatus(user.getId(), "active")
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        subscription.setStatus("canceled");
        subscription.setCanceledAt(LocalDateTime.now());
        subscription.setAutoRenew(false);
        subscription.setCancellationReason(request.getReason());

        subscriptionRepository.save(subscription);

        // Send cancellation confirmation email
        emailService.sendSubscriptionCancellation(user.getEmail(), subscription);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Subscription canceled successfully");
        response.put("accessUntil", subscription.getEndDate());

        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserSubscriptions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Subscription> subscriptions = subscriptionRepository.findByUserId(user.getId());

        List<Map<String, Object>> subscriptionData = subscriptions.stream()
                .map(this::mapSubscriptionToResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("subscriptions", subscriptionData);
        response.put("total", subscriptions.size());

        return response;
    }

    private Map<String, Object> mapSubscriptionToResponse(Subscription sub) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", sub.getId());
        data.put("planType", sub.getPlanType());
        data.put("status", sub.getStatus());
        data.put("amount", sub.getAmount());
        data.put("currency", sub.getCurrency());
        data.put("createdAt", sub.getCreatedAt());
        data.put("nextBillingDate", sub.getNextBillingDate());
        data.put("autoRenew", sub.getAutoRenew());
        return data;
    }
}