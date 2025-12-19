package com.chico.chico.service;

import com.chico.chico.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {

    NotificationDTO getNotification(Long id);

    List<NotificationDTO> viewAllNotifications();

    void deleteNotification(Long notificationId);
}
