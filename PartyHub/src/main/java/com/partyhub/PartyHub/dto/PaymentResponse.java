package com.partyhub.PartyHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private String chargeId;
    private Long amount;
    private String currency;
    private String description;
}
