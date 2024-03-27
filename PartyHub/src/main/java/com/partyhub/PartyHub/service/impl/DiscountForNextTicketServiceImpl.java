package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.entities.DiscountForNextTicket;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.entities.UserDetails;
import com.partyhub.PartyHub.exceptions.DiscountForNextTicketNotFoundException;
import com.partyhub.PartyHub.repository.DiscountForNextTicketRepository;
import com.partyhub.PartyHub.service.DiscountForNextTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiscountForNextTicketServiceImpl implements DiscountForNextTicketService {

    private final DiscountForNextTicketRepository discountForNextTicketRepository;


    @Override
    public void addOrUpdateDiscountForUser(User user, Event event, int increaseAmount) {
        UserDetails userDetails = user.getUserDetails();
        Optional<DiscountForNextTicket> existingDiscount = discountForNextTicketRepository.findByUserDetailsAndEvent(userDetails, event);
        if (existingDiscount.isPresent()) {
            DiscountForNextTicket discount = existingDiscount.get();
            discount.setValue(discount.getValue() + increaseAmount);
            discountForNextTicketRepository.save(discount);
        } else {
            DiscountForNextTicket newDiscount = new DiscountForNextTicket();
            newDiscount.setUserDetails(userDetails);
            newDiscount.setEvent(event);
            newDiscount.setValue(increaseAmount);
            discountForNextTicketRepository.save(newDiscount);
        }
    }

    @Override
    public DiscountForNextTicket findDiscountForUserAndEvent(UserDetails userDetails, Event event) {
        return discountForNextTicketRepository.findByUserDetailsAndEvent(userDetails, event)
                .orElseThrow(() -> new DiscountForNextTicketNotFoundException("DiscountForNextTicket not found for user and event"));
    }

    @Override
    public void useDiscountForNextTicket(DiscountForNextTicket discountForNextTicket) {
        // Logic to mark a discount as used, which could be deleting it or updating a status
        // For example, if deleting:
        discountForNextTicketRepository.delete(discountForNextTicket);
    }

    @Override
    public void saveDiscountForNextTicket(DiscountForNextTicket discountForNextTicket) {
        discountForNextTicketRepository.save(discountForNextTicket);
    }
}
