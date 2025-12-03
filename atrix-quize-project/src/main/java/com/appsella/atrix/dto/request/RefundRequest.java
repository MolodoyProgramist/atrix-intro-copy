package com.appsella.atrix.dto.request;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RefundRequest {
    @NotBlank @Email
    private String email;

    @NotBlank
    private String paymentDate;

    @NotBlank
    private String paymentMethod;

    @NotBlank
    private String reason;

    private String paymentReceipt;
}
