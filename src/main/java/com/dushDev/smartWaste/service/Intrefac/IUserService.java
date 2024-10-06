package com.dushDev.smartWaste.service.Intrefac;

import com.dushDev.smartWaste.Dto.LoginRequest;
import com.dushDev.smartWaste.Dto.Response;
import com.dushDev.smartWaste.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface IUserService {
    Response register(User user);
    Response login (LoginRequest loginRequest);
    Response getAllUser();
    Response resetPassword(Long userId,String email, String password);
    Response addUserProfile(Long userId, MultipartFile photo);
    Response updateUserProfile(Long userId, String name, String phoneNumber, String email, String address, MultipartFile photo);
    Response deleteUser(String userId);
    Response getUserById(String userId);
    Response getMyInfo(String email);
}
