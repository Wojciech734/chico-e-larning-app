package com.chico.chico.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;


    @Override
    public void sendVerificationEmail(String to, String token) {
        String subject = "Potwierdź swoje konto";
        String confirmationUrl = "http://localhost:8080/api/auth/verify?token=" + token;
        String content = """
                <p>Witaj,</p>
                <p>Dziękujemy za rejestrację. Kliknij w link, aby aktywować swoje konto:</p>
                <p><a href="%s">Potwierdź konto</a></p>
                <p>Jeżeli to nie Ty zakładałeś konto - zignoruj tę wiadomość.</p>
                """.formatted(confirmationUrl);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("error: cannot send an email", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Reset hasła";
        String passwordResetUrl = "http://localhost:8080/api/auth/reset-password?token=" + token;
        String content = """
                <p>Witaj, </p>
                <p>Kliknij w link, aby zresetować swoje hasło:</p>
                <p><a href="%s">Resetuj hasło</a>
                <p>Jeżeli to nie Ty - zignoruj tę wiadomość.</p>
                """.formatted(passwordResetUrl);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("error: cannot send an email", e);
        }
    }

    @Override
    public void sendEmailResetEmail(String to, String token) {

    }
}
