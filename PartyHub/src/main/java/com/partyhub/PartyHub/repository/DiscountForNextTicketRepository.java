package com.partyhub.PartyHub.repository;

import com.partyhub.PartyHub.entities.DiscountForNextTicket;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface DiscountForNextTicketRepository extends JpaRepository<DiscountForNextTicket, UUID> {
    Optional<DiscountForNextTicket> findByUserDetailsAndEvent(UserDetails userDetails, Event event);
}