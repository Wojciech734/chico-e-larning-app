package com.chico.chico.controller;

import com.chico.chico.dto.NotificationDTO;
import com.chico.chico.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("notification/{notificationId}")
    public NotificationDTO getNotification(@PathVariable("notificationId") Long notificationId) {
        return notificationService.getNotification(notificationId);
    }

}
