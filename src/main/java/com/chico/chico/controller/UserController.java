package com.chico.chico.controller;

import com.chico.chico.dto.UserDTO;
import com.chico.chico.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/teacher/{id}")
    public UserDTO getTeacherProfile(@RequestParam Long id) {
        return userService.getTeacherProfile(id);
    }

}
