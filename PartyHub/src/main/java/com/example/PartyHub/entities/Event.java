package com.example.PartyHub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String mainBanner;
    private String secondaryBanner;
    private String location;
    private LocalDate date;
    private String details;
    private int price;
    private int ticketsNumber;
    @OneToMany(mappedBy = "event")
    private List<Ticket> tickets;
}
