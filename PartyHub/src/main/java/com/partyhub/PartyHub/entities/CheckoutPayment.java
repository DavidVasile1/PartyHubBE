package com.partyhub.PartyHub.entities;

import lombok.Data;

import java.util.UUID;

@Data
public class CheckoutPayment {
    private UUID eventId;
    private String token;
    private String userEmail;
    private String referalEmail;
    private String discountCode;
    private int quantity;
}