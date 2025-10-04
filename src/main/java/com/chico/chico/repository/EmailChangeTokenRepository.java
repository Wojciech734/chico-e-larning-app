package com.chico.chico.repository;

import com.chico.chico.entity.EmailChangeToken;
import com.chico.chico.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailChangeTokenRepository extends JpaRepository<EmailChangeToken, Long> {
    Optional<EmailChangeToken> findByToken(String token);
    Optional<EmailChangeToken> findByUser(User user);
}
