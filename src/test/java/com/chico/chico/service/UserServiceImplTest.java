package com.chico.chico.service;

import com.chico.chico.dto.UserDTO;
import com.chico.chico.entity.*;
import com.chico.chico.repository.EmailChangeTokenRepository;
import com.chico.chico.repository.PasswordResetTokenRepository;
import com.chico.chico.repository.UserRepository;
import com.chico.chico.repository.VerificationTokenRepository;
import com.chico.chico.request.EmailChangeRequest;
import com.chico.chico.request.LoginRequest;
import com.chico.chico.request.RegisterRequest;
import com.chico.chico.response.AuthResponse;
import com.chico.chico.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceImplTest {

    private UserRepository userRepository;
    private JwtProvider jwtProvider;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private MailService mailService;
    private VerificationTokenRepository verificationTokenRepository;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private EmailChangeTokenRepository emailChangeTokenRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtProvider = mock(JwtProvider.class);
        passwordEncoder = mock(PasswordEncoder.class);
        mailService = mock(MailService.class);
        verificationTokenRepository = mock(VerificationTokenRepository.class);
        passwordResetTokenRepository = mock(PasswordResetTokenRepository.class);
        emailChangeTokenRepository = mock(EmailChangeTokenRepository.class);
        userService = new UserServiceImpl(
                userRepository,
                jwtProvider,
                passwordEncoder,
                verificationTokenRepository,
                mailService,
                passwordResetTokenRepository,
                emailChangeTokenRepository)
        ;
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register(request));
        assertEquals("Email is already in use", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldSaveUserWithEncodedPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("firstName");
        request.setLastName("lastName");
        request.setEmail("test@example.com");
        request.setPassword("testPassword");
        request.setRoles(Set.of(Role.valueOf("STUDENT")));
        request.setCreatedAt(LocalDateTime.now());

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        AuthResponse res = userService.register(request);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("encodedPassword", savedUser.getPassword());
        assertNotEquals("testPassword", savedUser.getPassword());
        assertEquals("test@example.com", res.getUser().getEmail());
    }

    @Test
    void register_ShouldSaveUserAndReturnAuthResponse_WhenSuccessful() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("firstName");
        request.setLastName("lastName");
        request.setEmail("test@example.com");
        request.setPassword("testPassword");
        request.setRoles(Set.of(Role.valueOf("STUDENT")));
        request.setCreatedAt(LocalDateTime.now());

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        AuthResponse res = userService.register(request);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("firstName", savedUser.getFirstName());
        assertEquals("lastName", savedUser.getLastName());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(Set.of(Role.valueOf("STUDENT")), savedUser.getRoles());

        assertNotNull(res);

        UserDTO dto = res.getUser();
        assertEquals("firstName", dto.getFirstName());
        assertEquals("lastName", dto.getLastName());
        assertEquals("test@example.com", dto.getEmail());
    }

    @Test
    void login_ShouldThrowExceptionIfUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("testPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login(request));

        assertEquals("invalid password or email", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void login_ShouldThrowException_WhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login(request));

        assertEquals("invalid password or email", exception.getMessage());
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    }

    @Test
    void login_ShouldReturnJwtAndUserDto_WhenSuccessful() {
        LoginRequest request = new LoginRequest();

        request.setEmail("test@example.com");
        request.setPassword("testPassword");

        User user = new User();
        user.setId(1L);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of(Role.valueOf("STUDENT")));
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("testPassword", "encodedPassword")).thenReturn(true);
        when(jwtProvider.generateToken("test@example.com")).thenReturn("jwt-token");

        AuthResponse res = userService.login(request);

        assertNotNull(res);
        assertEquals("jwt-token", res.getToken());

        UserDTO dto = res.getUser();
        assertEquals(1L, dto.getId());
        assertEquals("firstName", dto.getFirstName());
        assertEquals("lastName", dto.getLastName());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals(Set.of(Role.valueOf("STUDENT")), dto.getRoles());

        verify(jwtProvider).generateToken("test@example.com");
    }

    @Test
    void verifyAccount_ShouldEnableUser_WhenTokenValid() {
        User user = new User();
        user.setEnabled(false);

        VerificationToken token = VerificationToken.builder()
                .token("valid")
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        when(verificationTokenRepository.findByToken("valid"))
                .thenReturn(Optional.of(token));

        userService.verifyAccount("valid");
        assertTrue(user.isEnabled());
        verify(userRepository).save(user);
        verify(verificationTokenRepository).delete(token);
    }

    @Test
    void verifyAccount_ShouldThrowException_WhenTokenExpired() {
        VerificationToken token = VerificationToken.builder()
                .token("expired")
                .user(new User())
                .expiryDate(LocalDateTime.now().minusMinutes(5))
                .build();

        when(verificationTokenRepository.findByToken("expired"))
                .thenReturn(Optional.of(token));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.verifyAccount("expired"));

        assertEquals("The token has expired", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void resendVerificationEmail_ShouldThrow_WhenUserAlreadyEnabled() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setEnabled(true);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.resendVerificationEmail("test@test.com"));

        assertEquals("This account has already been verified", exception.getMessage());
        verify(mailService, never()).sendVerificationEmail(any(), any());
    }

    @Test
    void resendVerificationEmail_ShouldSendNewToken_WhenUserNotVerified() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setEnabled(false);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(verificationTokenRepository.findByUser(user)).thenReturn(Optional.empty());

        userService.resendVerificationEmail("test@test.com");

        verify(verificationTokenRepository).save(any());
        verify(mailService).sendVerificationEmail(eq("test@test.com"), any());
    }

    @Test
    void forgotPassword_ShouldSendResetEmail_WhenUserExists() {
        User user = new User();
        user.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(Optional.empty());

        userService.forgotPassword("test@test.com");
        verify(passwordResetTokenRepository).save(any());
        verify(mailService).sendPasswordResetEmail(eq("test@test.com"), any());
    }

    @Test
    void forgotPassword_shouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.forgotPassword("test@test.com"));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void resetPassword_ShouldUpdatePassword_WhenTokenValid() {
        User user = new User();
        user.setPassword("oldOne");

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token("valid")
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(65))
                .build();

        when(passwordResetTokenRepository.findByToken("valid"))
                .thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        userService.resetPassword("valid", "newPassword");

        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).delete(resetToken);
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void requestEmailChange_ShouldSendEmail_WhenValid() {
        EmailChangeRequest request = new EmailChangeRequest();
        request.setNewEmail("new@email.com");

        User user = new User();
        user.setEmail("old@email.com");

        when(userRepository.findByEmail("new@email.com")).thenReturn(Optional.empty());
        when(jwtProvider.extractEmailFromToken("token")).thenReturn("old@email.com");
        when(userRepository.findByEmail("old@email.com")).thenReturn(Optional.of(user));
        when(emailChangeTokenRepository.findByUser(user)).thenReturn(Optional.empty());

        userService.requestEmailChange("Bearer token", request);

        verify(emailChangeTokenRepository).save(any());
        verify(mailService).sendEmailChangeEmail(eq("old@email.com"), any());
    }

    @Test
    void requestEmailChangeConfirmation_ShouldSendConfirmationEmail() {
        EmailChangeToken emailChangeToken = EmailChangeToken.builder()
                .token("valid")
                .newEmail("new@email.com")
                .build();

        when(emailChangeTokenRepository.findByToken("valid"))
                .thenReturn(Optional.of(emailChangeToken));

        userService.requestEmailChangeConfirmation("valid");

        verify(mailService).sendEmailChangeConfirmation("new@email.com", "valid");
    }

    @Test
    void confirmationChange_ShouldUpdateEmail_WhenTokenValid() {
        User user = new User();
        user.setEmail("old@email.com");

        EmailChangeToken emailChangeToken = EmailChangeToken.builder()
                .token("valid")
                .newEmail("new@email.com")
                .user(user)
                .build();

        when(emailChangeTokenRepository.findByToken("valid"))
                .thenReturn(Optional.of(emailChangeToken));

        userService.confirmEmailChange("valid");

        assertEquals("new@email.com", user.getEmail());
        verify(userRepository).save(user);
        verify(emailChangeTokenRepository).delete(emailChangeToken);
    }
}