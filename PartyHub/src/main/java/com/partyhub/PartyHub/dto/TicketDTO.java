package com.partyhub.PartyHub.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TicketDTO {
    private UUID ticketId;
    private LocalDateTime validationDate;
    private String eventName;
    private String eventLocation;
    private String eventCity;
    private LocalDate eventDate;
}
