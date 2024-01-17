package com.partyhub.PartyHub.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class RegisterDto {
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 5, message = "Full name must be more than 5 characters")
    private String fullName;

    @Min(value = 18, message = "You must be at least 18 years old")
    @Max(value = 100, message = "Age seems incorrect")
    private int age;
}
