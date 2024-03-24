package com.partyhub.PartyHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTicketInfoDTO {
    private BigDecimal price;
    private float discount;
    private int ticketsLeft;
    private int tickets;
    private int discountForNextTicket;
}