package com.appsella.atrix.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuizOptionData {
    private String id;
    private String text;
    private String icon;
}
