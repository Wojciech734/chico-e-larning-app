package com.chico.chico.service;

import com.chico.chico.dto.UserDTO;
import com.chico.chico.entity.*;
import com.chico.chico.exception.*;
import com.chico.chico.repository.EmailChangeTokenRepository;
import com.chico.chico.repository.PasswordResetTokenRepository;
import com.chico.chico.repository.UserRepository;
import com.chico.chico.repository.VerificationTokenRepository;
import com.chico.chico.request.EmailChangeRequest;
import com.chico.chico.request.LoginRequest;
import com.chico.chico.request.RegisterRequest;
import com.chico.chico.response.AuthResponse;
import com.chico.chico.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailChangeTokenRepository emailChangeTokenRepository;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyInUseException("Email is already in use");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(request.getRoles());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
        verificationTokenRepository.save(verificationToken);
        mailService.sendVerificationEmail(user.getEmail(), token);

        return new AuthResponse(null, mapToDTO(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidPasswordOrEmailException("invalid password or email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordOrEmailException("invalid password or email");
        }

        if (!user.isEnabled()) {
            throw new AccountVerificationException("Your account hasn't been activated yet");
        }

        String token = jwtProvider.generateToken(user.getEmail());
        return new AuthResponse(token, mapToDTO(user));
    }

    @Override
    public void verifyAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("invalid verification token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("The token has expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new AccountVerificationException("This account has already been verified");
        }

        verificationTokenRepository.findByUser(user)
                .ifPresent(verificationTokenRepository::delete);

        String newToken = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(newToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        verificationTokenRepository.save(verificationToken);
        mailService.sendVerificationEmail(user.getEmail(), newToken);
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        passwordResetTokenRepository.findByUser(user)
                .ifPresent(passwordResetTokenRepository::delete);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .build();

        passwordResetTokenRepository.save(resetToken);
        mailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }

    @Override
    public void requestEmailChange(String jwtToken, EmailChangeRequest request) {
        if (userRepository.findByEmail(request.getNewEmail()).isPresent()) {
            throw new EmailAlreadyInUseException("Email is already in use");
        }

        String email = jwtProvider.extractEmailFromToken(jwtToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        emailChangeTokenRepository.findByUser(user)
                .ifPresent(emailChangeTokenRepository::delete);

        String token = UUID.randomUUID().toString();
        EmailChangeToken changeToken = EmailChangeToken.builder()
                .token(token)
                .user(user)
                .newEmail(request.getNewEmail())
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .build();

        emailChangeTokenRepository.save(changeToken);
        mailService.sendEmailChangeEmail(user.getEmail(), token);
    }

    @Override
    public void requestEmailChangeConfirmation(String token) {
        EmailChangeToken confirmationToken = emailChangeTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid email change token"));
        mailService.sendEmailChangeConfirmation(confirmationToken.getNewEmail(), confirmationToken.getToken());
    }

    @Override
    public void confirmEmailChange(String token) {
        EmailChangeToken confirmationToken = emailChangeTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid email change token"));
        User user = confirmationToken.getUser();

        user.setEmail(confirmationToken.getNewEmail());
        userRepository.save(user);
        emailChangeTokenRepository.delete(confirmationToken);
    }

    @Override
    public UserDTO getTeacherProfile(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new UserNotFoundException("Teacher not found"));
        if (!teacher.isPublicProfile() || !teacher.getRoles().contains(Role.TEACHER)) {
            throw new TeacherProfileException("This teacher profile is private or it's not teacher profile");
        }
        return mapToDTO(teacher);
    }

    @Override
    public UserDTO getCurrentUser() {

        // Using Spring Security context to retrieve authenticated user
        // instead of manually parsing JWT token from request header.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

//        String email = jwtProvider.extractEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return mapToDTO(user);
    }

    @Override
    public void becomeTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Set<Role> roles = user.getRoles();

        roles.add(Role.TEACHER);

        user.setRoles(roles);
        userRepository.save(user);
    }

    private UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAvatarImage(),
                user.getBio(),
                user.isPublicProfile(),
                user.getRoles(),
                user.getCreatedAt(),
                user.isEnabled()
        );
    }
}