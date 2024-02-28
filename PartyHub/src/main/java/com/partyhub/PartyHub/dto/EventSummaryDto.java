package com.partyhub.PartyHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventSummaryDto{
    private UUID id;
    private String name;
    private String city;
    private LocalDate date;
}
