package com.partyhub.PartyHub.entities;

import lombok.Data;

@Data
public class CheckoutPayment {
    private String name;
    private String currency;
    private String successUrl;
    private String cancelUrl;
    private long amount;
    private long quantity;

}