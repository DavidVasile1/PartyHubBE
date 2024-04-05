package com.partyhub.PartyHub.service.impl;


import com.partyhub.PartyHub.dto.TicketDTO;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.Ticket;
import com.partyhub.PartyHub.exceptions.EventNotFoundException;
import com.partyhub.PartyHub.exceptions.TicketNotFoundException;
import com.partyhub.PartyHub.repository.EventRepository;
import com.partyhub.PartyHub.repository.TicketRepository;
import com.partyhub.PartyHub.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {


    private TicketRepository ticketRepository;
    private EventRepository eventRepository;


    @Override
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket generateAndSaveTicketForEvent(float pricePaid, String type, UUID eventId, LocalDateTime validationDate) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));


        Ticket ticket = new Ticket();
        ticket.setValidationDate(validationDate);
        ticket.setType(type);
        ticket.setEvent(event);


        return ticketRepository.save(ticket);
    }
    @Override
    public Ticket findById(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with ID: " + ticketId));
    }
    @Override
    public List<TicketDTO> getAllTicketsByEmail(String email) {
        List<Ticket> tickets = ticketRepository.findByEmail(email);

        return tickets.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private TicketDTO mapToDto(Ticket ticket) {
        // Assuming Event is eagerly fetched with the ticket or otherwise efficiently loaded
        Event event = ticket.getEvent();

        return new TicketDTO(
                ticket.getId(),
                ticket.getValidationDate(),
                event.getName(),
                event.getLocation(),
                event.getCity(),
                event.getDate()
        );
    }


}
