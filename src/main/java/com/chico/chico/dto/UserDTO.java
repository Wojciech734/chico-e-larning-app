package com.chico.chico.dto;

import com.chico.chico.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String avatarImage;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private boolean isEnabled;
}
