package com.appsella.atrix.dto.request;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SubscriptionCreateRequest {
    @NotBlank @Email
    private String email;

    @NotBlank
    private String planType;

    @NotBlank
    private String paymentMethod;

    private String paymentToken;
}
