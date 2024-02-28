package com.partyhub.PartyHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChargeRequest {
    private Long amount;
    private String currency;
    private String description;
    private String token;

}
