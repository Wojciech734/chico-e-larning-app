package com.chico.chico.repository;

import com.chico.chico.entity.PasswordResetToken;
import com.chico.chico.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
}
