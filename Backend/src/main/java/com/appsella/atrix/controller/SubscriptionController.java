package com.appsella.atrix.controller;

import com.appsella.atrix.dto.request.CancelSubscriptionRequest;
import com.appsella.atrix.dto.request.SubscriptionCreateRequest;
import com.appsella.atrix.dto.response.ApiResponse;
import com.appsella.atrix.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createSubscription(
            @Valid @RequestBody SubscriptionCreateRequest request) {
        log.info("Creating subscription for: {}", request.getEmail());
        Map<String, Object> response = subscriptionService.createSubscription(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cancelSubscription(
            @Valid @RequestBody CancelSubscriptionRequest request) {
        log.info("Canceling subscription for: {}", request.getEmail());
        Map<String, Object> response = subscriptionService.cancelSubscription(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserSubscriptions(
            @PathVariable String email) {
        log.info("Getting subscriptions for: {}", email);
        Map<String, Object> subscriptions = subscriptionService.getUserSubscriptions(email);
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }
}
