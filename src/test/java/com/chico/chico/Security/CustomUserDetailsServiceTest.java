package com.chico.chico.Security;

import com.chico.chico.entity.Role;
import com.chico.chico.entity.User;
import com.chico.chico.repository.UserRepository;
import com.chico.chico.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_Should_ReturnUserDetails_WhenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("name");
        user.setLastName("lastName");
        user.setEmail("test@email.com");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of(Role.TEACHER));
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@email.com");

        assertNotNull(userDetails);
        assertEquals("test@email.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("test@email.com"));
        assertEquals("User not found", exception.getMessage());
    }
}
