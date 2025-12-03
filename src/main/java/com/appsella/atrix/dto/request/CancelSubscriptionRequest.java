package com.appsella.atrix.dto.request;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class CancelSubscriptionRequest {
    @NotBlank @Email
    private String email;

    private String reason;
}
