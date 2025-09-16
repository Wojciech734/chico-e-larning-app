package com.chico.chico.service;

public interface MailService {
    void sendVerificationEmail(String to, String token);
}
