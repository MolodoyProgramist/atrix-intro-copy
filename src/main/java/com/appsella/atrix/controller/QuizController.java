package com.appsella.atrix.controller;

import com.appsella.atrix.dto.request.QuizAnswerRequest;
import com.appsella.atrix.dto.request.QuizStartRequest;
import com.appsella.atrix.dto.response.*;
import com.appsella.atrix.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<QuizStartResponse>> startQuiz(
            @Valid @RequestBody QuizStartRequest request) {
        log.info("Starting quiz with UTM data: {}", request);
        QuizStartResponse response = quizService.startQuiz(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{sessionId}/step")
    public ResponseEntity<ApiResponse<QuizStepResponse>> getStep(
            @PathVariable String sessionId) {
        log.info("Getting step for session: {}", sessionId);
        QuizStepResponse response = quizService.getStep(sessionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> submitAnswer(
            @PathVariable String sessionId,
            @Valid @RequestBody QuizAnswerRequest request) {
        log.info("Submitting answer for session: {}, step: {}", sessionId, request.getStepId());
        Map<String, Object> response = quizService.saveAnswer(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{sessionId}/results")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getResults(
            @PathVariable String sessionId) {
        log.info("Getting results for session: {}", sessionId);
        Map<String, Object> results = quizService.getResults(sessionId);
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}