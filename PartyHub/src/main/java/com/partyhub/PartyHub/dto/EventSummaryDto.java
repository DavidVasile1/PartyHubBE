package com.partyhub.PartyHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventSummaryDto{
    private String name;
    private String city;
    private LocalDate date;
}
