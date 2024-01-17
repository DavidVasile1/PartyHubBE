package com.partyhub.PartyHub.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class ProfileDto {
    private UUID userId;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 5, message = "Full name must be more than 5 characters")
    private String fullName;

    @Min(value = 18, message = "You must be at least 18 years old")
    @Max(value = 100, message = "Age seems incorrect")
    private int age;
    private int discountForNextTicket;
}