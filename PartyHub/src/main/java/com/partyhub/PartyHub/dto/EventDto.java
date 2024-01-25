package com.partyhub.PartyHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class EventDto {
    private UUID id;
    private String name;
    private String mainBanner;
    private String secondaryBanner;
    private String location;
    private LocalDate date;
    private String details;
    private float price;
    private float discount;
    private int ticketsNumber;
    private int ticketsLeft;
}
