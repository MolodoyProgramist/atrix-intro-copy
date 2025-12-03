package com.appsella.atrix.service;

import com.appsella.atrix.entity.QuizSession;
import com.appsella.atrix.entity.User;
import com.appsella.atrix.repository.QuizSessionRepository;
import com.appsella.atrix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final QuizSessionRepository quizSessionRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.count();
        long totalQuizCompletions = quizSessionRepository.countByCompletedTrue();
        long activeSubscriptions = 0; // Можно добавить подсчет

        stats.put("totalUsers", totalUsers);
        stats.put("totalQuizCompletions", totalQuizCompletions);
        stats.put("activeSubscriptions", activeSubscriptions);
        stats.put("conversionRate", calculateConversionRate());

        return stats;
    }

    public Map<String, Object> getUTMAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        // Аналитика по UTM источникам
        List<User> users = userRepository.findAll();
        Map<String, Long> sourceCounts = new HashMap<>();

        for (User user : users) {
            String source = user.getUtmSource() != null ? user.getUtmSource() : "direct";
            sourceCounts.put(source, sourceCounts.getOrDefault(source, 0L) + 1);
        }

        analytics.put("sources", sourceCounts);
        analytics.put("totalCampaigns", users.stream().map(User::getUtmCampaign).distinct().count());

        return analytics;
    }

    private double calculateConversionRate() {
        long totalUsers = userRepository.count();
        long usersWithSubscription = 0; // Можно добавить подсчет пользователей с подпиской

        if (totalUsers == 0) return 0.0;
        return (double) usersWithSubscription / totalUsers * 100;
    }
}
