package com.appsella.atrix.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class QuizStepResponse {
    private boolean success;
    private QuizStepData step;
    private ProgressData progress;
}
