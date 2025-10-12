package com.chico.chico.security;

import com.chico.chico.entity.Role;
import com.chico.chico.entity.User;
import com.chico.chico.repository.UserRepository;
import com.chico.chico.service.MailService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = authToken.getPrincipal();
        String registrationId = authToken.getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String firstName = (String) attributes.getOrDefault("given_name", "");
        String lastName = (String) attributes.getOrDefault("family_name", "");
        String avatar = (String) attributes.getOrDefault("picture", null);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            if (user.getProvider() == null || !user.getProvider().equals(registrationId)) {
                user.setProvider(registrationId);
                user.setProviderId(providerId);
                user.setAvatarImage(avatar);
                userRepository.save(user);
            }
        } else {
            User newUser = new User();
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setEmail(email);
            newUser.setAvatarImage(avatar);
            newUser.setProvider(registrationId);
            newUser.setProviderId(providerId);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setEnabled(true);
            newUser.setRoles(Set.of(Role.STUDENT));
            userRepository.save(newUser);
            user = newUser;
        }

        String token = jwtProvider.generateToken(user.getEmail());

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
