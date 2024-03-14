package com.partyhub.PartyHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ChargeRequest {
    private UUID eventId;
    private int tickets;
    private String token;
    private String userEmail;
    private String referralEmail;
    private String discountCode;
}
