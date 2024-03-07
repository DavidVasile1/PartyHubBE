package com.partyhub.PartyHub.service.impl;


import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.Ticket;
import com.partyhub.PartyHub.repository.EventRepository;
import com.partyhub.PartyHub.repository.TicketRepository;
import com.partyhub.PartyHub.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private EventRepository eventRepository;


    @Override
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Ticket generateAndSaveTicketForEvent(float pricePaid, String type, UUID eventId, LocalDate chosenDate) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();

            Ticket newTicket = new Ticket();
            newTicket.setValidationDate(chosenDate);
            newTicket.setPricePaid(pricePaid);
            newTicket.setType(type);
            newTicket.setEvent(event);

            return ticketRepository.save(newTicket);
        } else {
            throw new IllegalArgumentException("Event with ID " + eventId + " not found");
        }
    }
}
