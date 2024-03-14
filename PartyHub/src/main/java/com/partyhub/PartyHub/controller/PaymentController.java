package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.ChargeRequest;
import com.partyhub.PartyHub.dto.PaymentResponse;
import com.partyhub.PartyHub.entities.Ticket;
import com.partyhub.PartyHub.service.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Discount;
import com.stripe.param.ChargeCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.partyhub.PartyHub.entities.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentController {

    private final EventService eventService;
    private final TicketService ticketService;
    private final EmailSenderService emailSenderService;
    private final DiscountService discountService;
    private final UserService userService;

    @Value("${stripe.keys.secret}")
    private String apiKey;

    @PostMapping("/charge")
    public PaymentResponse chargeCard(@RequestBody ChargeRequest chargeRequest) throws StripeException {
        Stripe.apiKey = apiKey;
        float discount = 0;
        if(chargeRequest.getDiscountCode() != ""){
            discount = this.discountService.findByCode(chargeRequest.getDiscountCode()).get().getDiscountValue();
            discount = discount * eventService.getEventById(chargeRequest.getEventId()).get().getPrice();
            this.discountService.deleteDiscountByCode(chargeRequest.getDiscountCode());
        }
        if(chargeRequest.getReferralEmail() != ""){
            discount = eventService.getEventById(chargeRequest.getEventId()).get().getPrice() * eventService.getEventById(chargeRequest.getEventId()).get().getDiscount() * chargeRequest.getTickets();
            int discountForNextTicket = this.userService.findByEmail(chargeRequest.getReferralEmail()).get().getUserDetails().getDiscountForNextTicket();
            User user =  this.userService.findByEmail(chargeRequest.getReferralEmail()).get();
            user.getUserDetails().setDiscountForNextTicket(discountForNextTicket + (int)(eventService.getEventById(chargeRequest.getEventId()).get().getDiscount()) * chargeRequest.getTickets());
            this.userService.save(user);
        }
        float price = chargeRequest.getTickets() * eventService.getEventById(chargeRequest.getEventId()).get().getPrice() * 100 - discount;

        String description = "Payment for " + chargeRequest.getTickets() + " tickets";

        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount((long)price)
                .setCurrency("RON")
                .setDescription(description)
                .setSource(chargeRequest.getToken())
                .build();

        Charge charge = Charge.create(params);

        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < chargeRequest.getTickets(); i++) {
            Ticket ticket = new Ticket(UUID.randomUUID(), null, 0, "ticket", eventService.getEventById(chargeRequest.getEventId()).get());
            tickets.add(ticketService.saveTicket(ticket));
        }

        String emailBody = tickets.stream()
                .map(invite -> "Ticket Code: " + invite.getId().toString())
                .collect(Collectors.joining("\n"));
        emailSenderService.sendEmail(chargeRequest.getUserEmail(), "Tickets", emailBody);

        return new PaymentResponse(charge.getId(), charge.getAmount(), charge.getCurrency(), charge.getDescription());
    }

}