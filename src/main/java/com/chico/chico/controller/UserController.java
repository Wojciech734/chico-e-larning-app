package com.chico.chico.controller;

import com.chico.chico.dto.UserDTO;
import com.chico.chico.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/teacher/{id}")
    public UserDTO getTeacherProfile(@PathVariable Long id) {
        return userService.getTeacherProfile(id);
    }


    @GetMapping("/me")
    public UserDTO getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PutMapping("/become-teacher")
    public ResponseEntity<String> becomeTeacher() {
        userService.becomeTeacher();
        return ResponseEntity.ok("Successfully added new role to account");
    }
}
