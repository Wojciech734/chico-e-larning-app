package com.chico.chico.service;

public interface MailService {
    void sendVerificationEmail(String to, String token);

    void sendPasswordResetEmail(String to, String token);

    void sendEmailResetEmail(String to, String token);
}
