package com.chico.chico.service;

import com.chico.chico.dto.NotificationDTO;
import com.chico.chico.entity.Course;
import com.chico.chico.entity.Notification;
import com.chico.chico.entity.User;
import com.chico.chico.exception.CourseNotFoundException;
import com.chico.chico.exception.NotificationNotFound;
import com.chico.chico.exception.UserNotFoundException;
import com.chico.chico.repository.CourseRepository;
import com.chico.chico.repository.NotificationRepository;
import com.chico.chico.repository.ReviewRepository;
import com.chico.chico.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ReviewRepository repository;

    @Override
    public NotificationDTO getNotification(Long id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFound("Notification not found"));

        return mapToDTO(notification);
    }

    @Override
    public List<NotificationDTO> viewAllNotifications() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return notificationRepository.findByUserId(user.getId());
    }


    @Override
    public void deleteNotification(Long notificationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Notification notification = notificationRepository
                .findById(notificationId).orElseThrow(() -> new NotificationNotFound("Notification not found"));

        notificationRepository.delete(notification);
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getCourseId()
        );
    }
}
