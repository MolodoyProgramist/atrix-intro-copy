package com.appsella.atrix.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class QuizStepData {
    private String id;
    private String type;
    private String title;
    private String question;
    private String description;
    private String placeholder;
    private String button;
    private List<QuizOptionData> options;
    private List<String> loadingMessages;
}
