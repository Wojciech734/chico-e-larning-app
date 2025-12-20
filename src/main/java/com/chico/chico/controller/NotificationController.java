package com.chico.chico.controller;

import com.chico.chico.dto.NotificationDTO;
import com.chico.chico.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("notification/{notificationId}")
    public NotificationDTO getNotification(@PathVariable("notificationId") Long notificationId) {
        return notificationService.getNotification(notificationId);
    }

    @GetMapping("/")
    public List<NotificationDTO> viewAllNotifications() {
        return notificationService.viewAllNotifications();
    }

    @DeleteMapping("/notification/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable("id") Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Successfully deleted notification");
    }
}
