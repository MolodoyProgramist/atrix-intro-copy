package com.appsella.atrix.service;

import com.appsella.atrix.dto.request.LoginRequest;
import com.appsella.atrix.dto.response.LoginResponse;
import com.appsella.atrix.dto.response.UserInfoData;
import com.appsella.atrix.entity.User;
import com.appsella.atrix.exception.ResourceNotFoundException;
import com.appsella.atrix.repository.UserRepository;
import com.appsella.atrix.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Unfortunately, we couldn't find this email address"));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Send personal link email
        emailService.sendPersonalLink(user.getEmail(), user.getPersonalLink());

        boolean hasActiveSubscription = subscriptionRepository
                .findByUserIdAndStatus(user.getId(), "active")
                .isPresent();

        return LoginResponse.builder()
                .success(true)
                .message("Link sent to your email")
                .personalLink(user.getPersonalLink())
                .user(UserInfoData.builder()
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .createdAt(user.getCreatedAt())
                        .hasActiveSubscription(hasActiveSubscription)
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean hasActiveSubscription = subscriptionRepository
                .findByUserIdAndStatus(user.getId(), "active")
                .isPresent();

        Map<String, Object> info = new HashMap<>();
        info.put("email", user.getEmail());
        info.put("firstName", user.getFirstName());
        info.put("lastName", user.getLastName());
        info.put("createdAt", user.getCreatedAt());
        info.put("personalLink", user.getPersonalLink());
        info.put("hasActiveSubscription", hasActiveSubscription);

        return info;
    }
}
