package com.partyhub.PartyHub.repository;

import com.partyhub.PartyHub.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByEmail(String email);
    void deleteByEventId(UUID eventId);
}
