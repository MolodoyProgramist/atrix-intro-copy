package com.appsella.atrix.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuizStartResponse {
    private boolean success;
    private String sessionId;
    private String message;
}
