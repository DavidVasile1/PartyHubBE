package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.Ticket;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TicketService {

    Ticket saveTicket(Ticket ticket);
    Ticket generateAndSaveTicketForEvent(float pricePaid, String type, UUID eventId, LocalDateTime chosenDate);
    Ticket findById(UUID ticketId);
}
