package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.DiscountForNextTicket;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.entities.UserDetails;

import java.util.Optional;

public interface DiscountForNextTicketService {
    void addOrUpdateDiscountForUser(User user, Event event, int increaseAmount);
    public Optional<DiscountForNextTicket> findDiscountForUserAndEvent(UserDetails userDetails, Event event);
    void useDiscountForNextTicket(DiscountForNextTicket discountForNextTicket);

}
