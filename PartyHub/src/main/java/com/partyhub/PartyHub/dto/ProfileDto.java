package com.partyhub.PartyHub.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProfileDto {
    private UUID userId;
    private String email;
    private String fullName;
    private int age;
    private int discountForNextTicket;
}