package com.dushDev.smartWaste.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.time.LocalDateTime;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long userId;


    private String name;

    private String email;

    private String password;


    private String role;

    private String phoneNumber;


    private String address;

    private String profilePicture;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
