package com.appsella.atrix.controller;

import com.appsella.atrix.dto.request.RefundRequest;
import com.appsella.atrix.dto.response.ApiResponse;
import com.appsella.atrix.service.RefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/refund")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RefundController {

    private final RefundService refundService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Map<String, Object>>> requestRefund(
            @Valid @RequestBody RefundRequest request) {
        log.info("Refund request for: {}", request.getEmail());
        Map<String, Object> response = refundService.requestRefund(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserRefunds(
            @PathVariable String email) {
        log.info("Getting refunds for: {}", email);
        Map<String, Object> refunds = refundService.getUserRefunds(email);
        return ResponseEntity.ok(ApiResponse.success(refunds));
    }
}
