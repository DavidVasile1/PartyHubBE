package com.partyhub.PartyHub.service.impl;


import com.partyhub.PartyHub.entities.Ticket;
import com.partyhub.PartyHub.repository.TicketRepository;
import com.partyhub.PartyHub.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public Ticket saveInvite(Ticket invite) {
        return ticketRepository.save(invite);
    }
}
