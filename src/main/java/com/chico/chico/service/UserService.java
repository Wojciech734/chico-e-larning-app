package com.chico.chico.service;

import com.chico.chico.dto.UserDTO;
import com.chico.chico.request.EmailChangeRequest;
import com.chico.chico.request.LoginRequest;
import com.chico.chico.request.RegisterRequest;
import com.chico.chico.response.AuthResponse;

public interface UserService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void verifyAccount(String token);
    void resendVerificationEmail(String email);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
    void requestEmailChange(String jwtToken,EmailChangeRequest request);
    void requestEmailChangeConfirmation(String token);
    void confirmEmailChange(String token);
    UserDTO getTeacherProfile(Long teacherId);
    UserDTO getCurrentUser();
    void becomeTeacher();
}
