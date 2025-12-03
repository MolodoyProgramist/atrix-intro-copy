package com.appsella.atrix.controller;

import com.appsella.atrix.dto.request.LoginRequest;
import com.appsella.atrix.dto.response.ApiResponse;
import com.appsella.atrix.dto.response.LoginResponse;
import com.appsella.atrix.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserInfo(
            @PathVariable String email) {
        log.info("Getting user info for: {}", email);
        Map<String, Object> userInfo = userService.getUserInfo(email);
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }
}
