package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.Ticket;

import java.time.LocalDate;
import java.util.UUID;

public interface TicketService {

    Ticket saveTicket(Ticket ticket);
    Ticket generateAndSaveTicketForEvent(float pricePaid, String type, UUID eventId, LocalDate chosenDate);
}
