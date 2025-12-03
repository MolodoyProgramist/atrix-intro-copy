package com.appsella.atrix.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class QuizAnswerRequest {
    @NotBlank
    private String stepId;

    private Object answer;

    private String email;
}
