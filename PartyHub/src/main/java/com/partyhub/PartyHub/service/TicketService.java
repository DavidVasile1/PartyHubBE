package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.dto.TicketDTO;
import com.partyhub.PartyHub.entities.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TicketService {

    Ticket saveTicket(Ticket ticket);
    Ticket generateAndSaveTicketForEvent(float pricePaid, String type, UUID eventId, LocalDateTime chosenDate);
    Ticket findById(UUID ticketId);
    List<TicketDTO> getAllTicketsByEmail(String email);
}
