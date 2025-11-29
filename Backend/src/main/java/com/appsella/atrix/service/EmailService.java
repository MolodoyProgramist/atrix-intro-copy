package com.appsella.atrix.service;

import com.appsella.atrix.entity.EmailLog;
import com.appsella.atrix.entity.Refund;
import com.appsella.atrix.entity.Subscription;
import com.appsella.atrix.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    @Value("${app.support-email:atrix.app@support-team.app}")
    private String supportEmail;

    // Убрана зависимость от app.company.name, используется значение по умолчанию
    private final String companyName = "Atrix";

    public void sendQuizResults(String email, Map<String, Object> results) {
        String subject = "Your Personalized Palm Reading Results";
        String body = buildQuizResultsEmail(results);
        sendEmail(email, subject, body, "quiz_results");
    }

    public void sendPersonalLink(String email, String personalLink) {
        String subject = "Your Personal Access Link - " + companyName;
        String body = "<html>\n" +
                "<body style=\"font-family: Arial, sans-serif; background-color: #1a1a2e; color: #f0f0f0; padding: 20px;\">\n" +
                "    <div style=\"max-width: 600px; margin: 0 auto; background-color: #272a3e; padding: 30px; border-radius: 10px;\">\n" +
                "        <h1 style=\"color: #657BFF;\">Welcome to " + companyName + "</h1>\n" +
                "        <p>Thank you for using " + companyName + "! Here's your personal access link:</p>\n" +
                "        <div style=\"background-color: #1a1a2e; padding: 20px; border-radius: 8px; margin: 20px 0;\">\n" +
                "            <a href=\"" + personalLink + "\" style=\"color: #657BFF; font-size: 18px; text-decoration: none;\">" + personalLink + "</a>\n" +
                "        </div>\n" +
                "        <p>Please save this link to access your account anytime.</p>\n" +
                "        <p style=\"margin-top: 30px; color: #888;\">\n" +
                "            If you didn't request this link, please ignore this email.<br>\n" +
                "            Need help? Contact us at <a href=\"mailto:" + supportEmail + "\" style=\"color: #657BFF;\">" + supportEmail + "</a>\n" +
                "        </p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        sendEmail(email, subject, body, "personal_link");
    }

    public void sendSubscriptionConfirmation(String email, Subscription subscription) {
        String subject = "Subscription Confirmed - " + companyName;
        String body = buildSubscriptionEmail(subscription);
        sendEmail(email, subject, body, "subscription");
    }

    public void sendSubscriptionCancellation(String email, Subscription subscription) {
        String subject = "Subscription Cancelled - " + companyName;
        String body = "<html>\n" +
                "<body style=\"font-family: Arial, sans-serif;\">\n" +
                "    <h2>Subscription Cancelled</h2>\n" +
                "    <p>Your subscription has been successfully cancelled.</p>\n" +
                "    <p>You will continue to have access until: " + subscription.getEndDate() + "</p>\n" +
                "    <p>We're sorry to see you go! If you have any feedback, please let us know.</p>\n" +
                "</body>\n" +
                "</html>";

        sendEmail(email, subject, body, "cancellation");
    }

    public void sendRefundRequest(String email, Refund refund) {
        String subject = "Refund Request Received - " + companyName;
        String body = "<html>\n" +
                "<body>\n" +
                "    <h2>Refund Request Received</h2>\n" +
                "    <p>We've received your refund request for $" + String.format("%.2f", refund.getAmount()) + "</p>\n" +
                "    <p>Request ID: " + refund.getId() + "</p>\n" +
                "    <p>Status: " + refund.getStatus() + "</p>\n" +
                "    <p>We will process your request within 3-5 business days.</p>\n" +
                "    <p>You will receive another email once the refund is processed.</p>\n" +
                "</body>\n" +
                "</html>";

        sendEmail(email, subject, body, "refund");
    }

    private void sendEmail(String to, String subject, String body, String emailType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(supportEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);

            logEmail(to, emailType, "sent", body, null);
            log.info("Email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            logEmail(to, emailType, "failed", body, e.getMessage());
        }
    }

    private void logEmail(String email, String type, String status,
                          String content, String error) {
        EmailLog emailLog = new EmailLog();
        emailLog.setRecipientEmail(email);
        emailLog.setEmailType(type);
        emailLog.setStatus(status);
        emailLog.setContent(content);
        emailLog.setErrorMessage(error);
        emailLog.setSentAt(LocalDateTime.now());

        emailLogRepository.save(emailLog);
    }

    private String buildQuizResultsEmail(Map<String, Object> results) {
        return "<html>\n" +
                "<body style=\"font-family: Arial, sans-serif; background-color: #1a1a2e; color: #f0f0f0;\">\n" +
                "    <div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">\n" +
                "        <h1 style=\"color: #657BFF;\">Your Personalized Results</h1>\n" +
                "        <p>Thank you for completing your palm reading!</p>\n" +
                "        <div style=\"background-color: #272a3e; padding: 20px; border-radius: 10px; margin: 20px 0;\">\n" +
                "            <h2>Your Results:</h2>\n" +
                "            <p>" + results.toString() + "</p>\n" +
                "        </div>\n" +
                "        <p>Access your full report anytime using your personal link.</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    private String buildSubscriptionEmail(Subscription subscription) {
        return "<html>\n" +
                "<body style=\"font-family: Arial, sans-serif;\">\n" +
                "    <h2>Subscription Confirmed!</h2>\n" +
                "    <p>Thank you for subscribing to " + companyName + " " + subscription.getPlanType() + " plan.</p>\n" +
                "    <div style=\"background-color: #f5f5f5; padding: 15px; margin: 20px 0;\">\n" +
                "        <p><strong>Plan:</strong> " + subscription.getPlanType() + "</p>\n" +
                "        <p><strong>Amount:</strong> $" + String.format("%.2f", subscription.getAmount()) + "</p>\n" +
                "        <p><strong>Next Billing:</strong> " + subscription.getNextBillingDate() + "</p>\n" +
                "    </div>\n" +
                "    <p>You now have full access to all premium features!</p>\n" +
                "</body>\n" +
                "</html>";
    }
}