package com.appsella.atrix.service;

import com.appsella.atrix.dto.request.QuizAnswerRequest;
import com.appsella.atrix.dto.request.QuizStartRequest;
import com.appsella.atrix.dto.response.*;
import com.appsella.atrix.entity.QuizSession;
import com.appsella.atrix.entity.User;
import com.appsella.atrix.exception.ResourceNotFoundException;
import com.appsella.atrix.repository.QuizSessionRepository;
import com.appsella.atrix.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Transactional
    public QuizStartResponse startQuiz(QuizStartRequest request) {
        String sessionId = UUID.randomUUID().toString();

        QuizSession session = QuizSession.builder()
                .sessionId(sessionId)
                .utmSource(request.getUtmSource())
                .utmCampaign(request.getUtmCampaign())
                .utmMedium(request.getUtmMedium())
                .adId(request.getAdId())
                .mode(request.getMode())
                .startedAt(LocalDateTime.now())
                .completed(false)
                .currentStep(0)
                .build();

        sessionRepository.save(session);
        log.info("Created quiz session: {}", sessionId);

        return QuizStartResponse.builder()
                .success(true)
                .sessionId(sessionId)
                .message("Quiz session created")
                .build();
    }

    public QuizStepResponse getStep(String sessionId) {
        QuizSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        List<QuizStepData> steps = getQuizSteps();

        if (session.getCurrentStep() >= steps.size()) {
            throw new IllegalStateException("Quiz already completed");
        }

        QuizStepData currentStep = steps.get(session.getCurrentStep());

        return QuizStepResponse.builder()
                .success(true)
                .step(currentStep)
                .progress(ProgressData.builder()
                        .current(session.getCurrentStep() + 1)
                        .total(steps.size())
                        .build())
                .build();
    }

    @Transactional
    public Map<String, Object> saveAnswer(String sessionId, QuizAnswerRequest request) {
        QuizSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        List<Map<String, Object>> answers = getAnswersList(session);

        Map<String, Object> answerData = new HashMap<>();
        answerData.put("stepId", request.getStepId());
        answerData.put("answer", request.getAnswer());
        answerData.put("timestamp", LocalDateTime.now().toString());
        answers.add(answerData);

        try {
            session.setQuizAnswers(objectMapper.writeValueAsString(answers));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to save answers", e);
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseGet(() -> createNewUser(request.getEmail(), session));

            session.setUser(user);
        }

        session.setCurrentStep(session.getCurrentStep() + 1);

        int totalSteps = getQuizSteps().size();
        boolean isCompleted = session.getCurrentStep() >= totalSteps;

        if (isCompleted) {
            session.setCompleted(true);
            session.setCompletedAt(LocalDateTime.now());
        }

        sessionRepository.save(session);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("nextStep", !isCompleted);
        response.put("message", "Answer saved");
        response.put("currentStep", session.getCurrentStep());

        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getResults(String sessionId) {
        QuizSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (!session.getCompleted()) {
            throw new IllegalStateException("Quiz not completed yet");
        }

        Map<String, Object> results = generatePersonalizedResults(session);

        if (session.getUser() != null) {
            emailService.sendQuizResults(session.getUser().getEmail(), results);
        }

        return results;
    }

    private List<Map<String, Object>> getAnswersList(QuizSession session) {
        if (session.getQuizAnswers() == null || session.getQuizAnswers().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(session.getQuizAnswers(), List.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing quiz answers", e);
            return new ArrayList<>();
        }
    }

    private User createNewUser(String email, QuizSession session) {
        String personalLink = "https://quiz.atrix.guide/app/" + UUID.randomUUID();

        return userRepository.save(User.builder()
                .email(email)
                .personalLink(personalLink)
                .utmSource(session.getUtmSource())
                .utmCampaign(session.getUtmCampaign())
                .utmMedium(session.getUtmMedium())
                .adId(session.getAdId())
                .createdAt(LocalDateTime.now())
                .build());
    }

    private Map<String, Object> generatePersonalizedResults(QuizSession session) {
        Map<String, Object> results = new HashMap<>();
        results.put("sessionId", session.getSessionId());
        results.put("completedAt", session.getCompletedAt());

        if (session.getUser() != null) {
            results.put("userId", session.getUser().getId());
            results.put("personalLink", session.getUser().getPersonalLink());
            results.put("email", session.getUser().getEmail());
        }

        List<Map<String, Object>> answers = getAnswersList(session);
        results.put("answers", answers);
        results.put("recommendations", generateRecommendations(answers));
        results.put("plan", generateMeditationPlan(answers));

        return results;
    }

    private Map<String, Object> generateRecommendations(List<Map<String, Object>> answers) {
        Map<String, Object> recommendations = new HashMap<>();
        recommendations.put("meditationStyle", "Guided Meditation");
        recommendations.put("duration", "10 minutes daily");
        recommendations.put("bestTime", "Morning");
        return recommendations;
    }

    private Map<String, Object> generateMeditationPlan(List<Map<String, Object>> answers) {
        Map<String, Object> plan = new HashMap<>();
        plan.put("week1", "Introduction to Meditation");
        plan.put("week2", "Breathing Techniques");
        plan.put("week3", "Body Scan");
        plan.put("week4", "Advanced Practice");
        return plan;
    }

    private List<QuizStepData> getQuizSteps() {
        List<QuizStepData> steps = new ArrayList<>();

        // Step 1: Intro
        steps.add(QuizStepData.builder()
                .id("intro")
                .type("intro")
                .title("Welcome to Your Palm Reading Journey")
                .description("Discover insights about your future in just 2 minutes")
                .button("Start Quiz")
                .build());

        // Step 2: Goal
        steps.add(QuizStepData.builder()
                .id("goal")
                .type("multiple_choice")
                .question("What is your main goal?")
                .options(Arrays.asList(
                        QuizOptionData.builder().id("health").text("Improve health").icon("🧘‍♀️").build(),
                        QuizOptionData.builder().id("wealth").text("Financial success").icon("💰").build(),
                        QuizOptionData.builder().id("love").text("Love and relationships").icon("❤️").build(),
                        QuizOptionData.builder().id("career").text("Career growth").icon("📈").build()
                ))
                .build());

        // Step 3: Birth Date
        steps.add(QuizStepData.builder()
                .id("birth_date")
                .type("date")
                .question("When were you born?")
                .description("This helps us provide more accurate insights")
                .placeholder("Select your birth date")
                .build());

        // Step 4: Gender
        steps.add(QuizStepData.builder()
                .id("gender")
                .type("multiple_choice")
                .question("What is your gender?")
                .options(Arrays.asList(
                        QuizOptionData.builder().id("female").text("Female").icon("👩").build(),
                        QuizOptionData.builder().id("male").text("Male").icon("👨").build(),
                        QuizOptionData.builder().id("other").text("Prefer not to say").icon("🙂").build()
                ))
                .build());

        // Step 5: Stress Level
        steps.add(QuizStepData.builder()
                .id("stress")
                .type("multiple_choice")
                .question("How would you describe your stress level?")
                .options(Arrays.asList(
                        QuizOptionData.builder().id("low").text("Low").icon("😊").build(),
                        QuizOptionData.builder().id("medium").text("Medium").icon("😐").build(),
                        QuizOptionData.builder().id("high").text("High").icon("😥").build(),
                        QuizOptionData.builder().id("very_high").text("Very High").icon("😫").build()
                ))
                .build());

        // Step 6: Sleep Quality
        steps.add(QuizStepData.builder()
                .id("sleep")
                .type("multiple_choice")
                .question("How is your sleep quality?")
                .options(Arrays.asList(
                        QuizOptionData.builder().id("excellent").text("Excellent").icon("😴").build(),
                        QuizOptionData.builder().id("good").text("Good").icon("🛌").build(),
                        QuizOptionData.builder().id("fair").text("Fair").icon("🥱").build(),
                        QuizOptionData.builder().id("poor").text("Poor").icon("😩").build()
                ))
                .build());

        // Step 7: Relationship Status
        steps.add(QuizStepData.builder()
                .id("relationship")
                .type("multiple_choice")
                .question("What is your relationship status?")
                .options(Arrays.asList(
                        QuizOptionData.builder().id("single").text("Single").icon("💁").build(),
                        QuizOptionData.builder().id("relationship").text("In a relationship").icon("💑").build(),
                        QuizOptionData.builder().id("married").text("Married").icon("💍").build(),
                        QuizOptionData.builder().id("complicated").text("It's complicated").icon("😅").build()
                ))
                .build());

        // Step 8: Career Satisfaction (FIXED)
        steps.add(QuizStepData.builder()
                .id("career_satisfaction")
                .type("multiple_choice")
                .question("How satisfied are you with your career?")
                .options(Arrays.asList(
                        QuizOptionData.builder().id("very_satisfied").text("Very Satisfied").icon("🌟").build(),
                        QuizOptionData.builder().id("satisfied").text("Satisfied").icon("👍").build(),
                        QuizOptionData.builder().id("neutral").text("Neutral").icon("🤔").build(),
                        QuizOptionData.builder().id("dissatisfied").text("Dissatisfied").icon("👎").build()
                ))
                .build());

        // Step 9: Email
        steps.add(QuizStepData.builder()
                .id("email")
                .type("email")
                .question("Where should we send your personalized results?")
                .description("We'll send your detailed palm reading analysis")
                .placeholder("Enter your email address")
                .button("Get My Results")
                .loadingMessages(Arrays.asList(
                        "Analyzing your palm lines...",
                        "Reading your life path...",
                        "Calculating your destiny...",
                        "Finalizing your personalized insights..."
                ))
                .build());

        return steps;
    }
}
