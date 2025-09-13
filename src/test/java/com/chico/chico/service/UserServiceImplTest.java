package com.chico.chico.service;

import com.chico.chico.dto.UserDTO;
import com.chico.chico.entity.Role;
import com.chico.chico.entity.User;
import com.chico.chico.repository.UserRepository;
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

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtProvider = mock(JwtProvider.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, jwtProvider, passwordEncoder);
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
        when(jwtProvider.generateToken(request.getEmail())).thenReturn("jwt-token");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        AuthResponse res = userService.register(request);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("encodedPassword", savedUser.getPassword());
        assertNotEquals("testPassword", savedUser.getPassword());

        assertEquals("jwt-token", res.getToken());
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
        when(jwtProvider.generateToken(request.getEmail())).thenReturn("jwt-token");

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
        assertEquals("jwt-token", res.getToken());

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
}
