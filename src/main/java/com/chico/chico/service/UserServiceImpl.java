package com.chico.chico.service;

import com.chico.chico.dto.UserDTO;
import com.chico.chico.entity.User;
import com.chico.chico.repository.UserRepository;
import com.chico.chico.request.LoginRequest;
import com.chico.chico.request.RegisterRequest;
import com.chico.chico.response.AuthResponse;
import com.chico.chico.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(request.getRoles());
        user.setCreatedAt(request.getCreatedAt());

        userRepository.save(user);

        String token = jwtProvider.generateToken(user.getEmail());
        return new AuthResponse(token, mapToDTO(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("invalid password or email"));

        if (!passwordEncoder.matches(user.getPassword(), request.getPassword())) {
            throw new RuntimeException("invalid password or email");
        }

        String token = jwtProvider.generateToken(user.getEmail());
        return new AuthResponse(token, mapToDTO(user));
    }

    private UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAvatarImage(),
                user.getRoles(),
                user.getCreatedAt()
        );
    }
}
