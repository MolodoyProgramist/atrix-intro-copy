package com.appsella.atrix.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String emailType; // "quiz_results", "personal_link", "subscription", "refund"

    @Column(nullable = false)
    private String status; // "sent", "failed", "pending"

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }
}
