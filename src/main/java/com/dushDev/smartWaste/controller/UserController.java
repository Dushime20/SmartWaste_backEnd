package com.dushDev.smartWaste.controller;

import com.dushDev.smartWaste.Dto.Response;
import com.dushDev.smartWaste.service.Intrefac.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Response> getAllUser(){
        Response response = userService.getAllUser();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-by-id/{userId}")
    public ResponseEntity<Response> getUserById(@PathVariable("userId") String userId){
        Response response = userService.getUserById(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Response> deleteUser(@PathVariable("userId") String userId){
        Response response = userService.deleteUser(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-logged-in-profile-info")
    public ResponseEntity<Response> getLoggedInProfileInfo(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Response response = userService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/add-profile/{userId}")
    public ResponseEntity<Response> addProfile(@PathVariable("userId") Long userId,
                                               @RequestParam("photo") MultipartFile photo) {

        System.out.println("Received request to update profile for user: " + userId);
        System.out.println("Received file: " + photo.getOriginalFilename());
        Response response = userService.addUserProfile(userId, photo);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PutMapping("/update-user/{userId}")
    public ResponseEntity<Response> updateUserProfile(@PathVariable("userId") Long userId,
                                                      @RequestParam String email,
                                                      @RequestParam String password,
                                                      @RequestParam String phoneNumber,
                                                      @RequestParam String address,
                                                      @RequestParam MultipartFile photo){
        Response response = userService.updateUserProfile(userId, email, password, phoneNumber, address, photo);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }
}
