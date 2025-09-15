package com.chico.chico.Security;

import com.chico.chico.entity.Role;
import com.chico.chico.entity.User;
import com.chico.chico.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CustomUserDetailsTest {

    private User user;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("name");
        user.setLastName("lastName");
        user.setEmail("test@email.com");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of(Role.STUDENT, Role.TEACHER));
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true);
        customUserDetails = new CustomUserDetails(user);
    }

    @Test
    void getAuthorities_ShouldReturnRolesPrefixedWithROLE() {
        var authorities = customUserDetails.getAuthorities();

        assertNotNull(authorities);
        assertEquals(2, authorities.size());

        assertTrue(authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_STUDENT")));
        assertTrue(authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_TEACHER")));
    }

    @Test
    void isEnabled_ShouldReturnUserEnabledStatus() {
        assertTrue(customUserDetails.isEnabled());

        user.setEnabled(false);
        customUserDetails = new CustomUserDetails(user);

        assertFalse(customUserDetails.isEnabled());
    }

    @Test
    void getPassword_ShouldReturnStoredPassword() {
        assertEquals("encodedPassword", customUserDetails.getPassword());
    }

    @Test
    void getUsername_ShouldReturnUserEmail() {
        assertEquals("test@email.com", customUserDetails.getUsername());
    }
}
