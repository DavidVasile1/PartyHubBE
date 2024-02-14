package com.partyhub.PartyHub.dto;

import jakarta.persistence.Lob;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public class EventDto {
    private UUID id;

    @NotEmpty(message = "The event name cannot be empty")
    private String name;

    @Lob
    private byte[] mainBanner;

    @Lob
    private byte[] secondaryBanner;

    @NotEmpty(message = "The event location cannot be empty")
    private String location;

    @NotEmpty(message = "The city cannot be empty")
    private String city;

    private float lng;
    private float lat;

    @NotNull(message = "The event date is required")
    @FutureOrPresent(message = "The event date must be in the present or future")
    private LocalDate date;

    private String details;

    @PositiveOrZero(message = "The price cannot be negative")
    private float price;

    @Min(value = 0, message = "The discount cannot be negative")
    @Max(value = 100, message = "The discount cannot be greater than 100")
    private float discount;

    @Positive(message = "The number of tickets must be positive")
    private int ticketsNumber;

    @PositiveOrZero(message = "The number of remaining tickets cannot be negative")
    private int ticketsLeft;
}
