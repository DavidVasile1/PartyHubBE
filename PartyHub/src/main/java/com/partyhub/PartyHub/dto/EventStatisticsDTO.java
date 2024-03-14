package com.partyhub.PartyHub.dto;

import com.partyhub.PartyHub.entities.Statistics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventStatisticsDTO {
    private String name;
    private String location;
    private LocalDate date;
    private float price;
    private float discount;
    private Statistics statistics;
}
