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

    public EventSummaryDto(String name, String city, LocalDate date) {
        this.name = name;
        this.city = city;
        this.date = date;
    }

    public EventSummaryDto(UUID id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }
}
