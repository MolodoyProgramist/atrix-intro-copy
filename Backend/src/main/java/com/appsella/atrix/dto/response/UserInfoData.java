package com.appsella.atrix.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserInfoData {
    private String email;
    private String firstName;
    private LocalDateTime createdAt;
    private Boolean hasActiveSubscription;
}