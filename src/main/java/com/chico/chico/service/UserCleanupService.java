package com.chico.chico.service;

import com.chico.chico.entity.User;
import com.chico.chico.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCleanupService {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    public void removeUnverifiedAccounts() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<User> usersToDelete = userRepository.findAll().stream()
                .filter(u -> !u.isEnabled() && u.getCreatedAt().isBefore(cutoff))
                .toList();
        if (!usersToDelete.isEmpty()) {
            userRepository.deleteAll(usersToDelete);
        }
    }

}
