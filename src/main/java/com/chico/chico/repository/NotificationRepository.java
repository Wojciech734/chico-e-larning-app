package com.chico.chico.repository;

import com.chico.chico.dto.NotificationDTO;
import com.chico.chico.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<NotificationDTO> findByUserId(Long id);
}
