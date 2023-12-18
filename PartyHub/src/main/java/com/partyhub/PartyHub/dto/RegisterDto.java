package com.partyhub.PartyHub.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String email;
    private String password;
    private String fullName;
    private int age;
}
