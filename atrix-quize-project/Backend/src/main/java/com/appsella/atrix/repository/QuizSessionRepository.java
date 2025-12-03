package com.appsella.atrix.repository;

import com.appsella.atrix.entity.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {
    Optional<QuizSession> findBySessionId(String sessionId);
    List<QuizSession> findByUserId(Long userId);
    List<QuizSession> findByCompletedTrue();
    List<QuizSession> findByCompletedFalseAndStartedAtBefore(LocalDateTime dateTime);
    long countByCompletedTrue();
}
