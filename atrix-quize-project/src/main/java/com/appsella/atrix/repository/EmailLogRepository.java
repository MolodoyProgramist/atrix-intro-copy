package com.appsella.atrix.repository;

import com.appsella.atrix.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    List<EmailLog> findByRecipientEmail(String email);
    List<EmailLog> findByStatus(String status);
    List<EmailLog> findByEmailType(String emailType);
}
