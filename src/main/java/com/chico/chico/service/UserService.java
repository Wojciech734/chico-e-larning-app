package com.chico.chico.service;

import com.chico.chico.entity.User;
import com.chico.chico.request.LoginRequest;
import com.chico.chico.request.RegisterRequest;
import com.chico.chico.response.AuthResponse;

public interface UserService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void verifyAccount(String token);
}
