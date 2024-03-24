package com.partyhub.PartyHub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statistics")
public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private int ticketsSold;
    private BigDecimal moneyEarned;
    private int generatedInvites;
    private int ticketBasedAttendees;
    private int invitationBasedAttendees;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

}
