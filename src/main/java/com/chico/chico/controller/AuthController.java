package com.chico.chico.controller;

import com.chico.chico.request.*;
import com.chico.chico.response.AuthResponse;
import com.chico.chico.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse res = userService.register(request);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse res = userService.login(request);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam String token) {
        userService.verifyAccount(token);
        return ResponseEntity.ok("Your account has been activated. You can login now");
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody EmailRequest request) {
        userService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok("New verification link has been send to your email");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest request) {
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Password reset link has been sent to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(token, request.getNewPassword());
        return ResponseEntity.ok("Password has been reset successfully");
    }
}
