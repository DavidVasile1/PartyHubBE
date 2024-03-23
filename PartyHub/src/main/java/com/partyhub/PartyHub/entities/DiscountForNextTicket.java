package com.partyhub.PartyHub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class DiscountForNextTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private int value;
    @ManyToOne
    private UserDetails userDetails;
    @ManyToOne
    private Event event;
}
