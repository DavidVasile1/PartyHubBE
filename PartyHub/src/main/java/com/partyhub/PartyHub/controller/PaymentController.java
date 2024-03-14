package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.ChargeRequest;
import com.partyhub.PartyHub.dto.PaymentResponse;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.Statistics;
import com.partyhub.PartyHub.entities.Ticket;
import com.partyhub.PartyHub.service.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Discount;
import com.stripe.param.ChargeCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.partyhub.PartyHub.entities.User;
import org.springframework.web.server.ResponseStatusException;

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
    private final StatisticsService statisticsService;

    @Value("${stripe.keys.secret}")
    private String apiKey;




    @PostMapping("/charge")
    public PaymentResponse chargeCard(@RequestBody ChargeRequest chargeRequest) throws StripeException {
        Stripe.apiKey = apiKey;

        Event event = eventService.getEventById(chargeRequest.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        float discount = calculateDiscount(chargeRequest, event);
        float price = (chargeRequest.getTickets() * event.getPrice() - discount) * 100;

        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount((long) price)
                .setCurrency("RON")
                .setDescription("Payment for " + chargeRequest.getTickets() + " tickets to " + event.getName())
                .setSource(chargeRequest.getToken())
                .build();

        Charge charge = Charge.create(params);

        List<Ticket> tickets = generateTickets(chargeRequest, event);
        sendTicketsEmail(chargeRequest.getUserEmail(), tickets);
        updateEventStatistics(event, chargeRequest.getTickets(), price / 100);

        return new PaymentResponse(charge.getId(), charge.getAmount(), charge.getCurrency(), charge.getDescription());
    }

    private float calculateDiscount(ChargeRequest chargeRequest, Event event) {
        float discount = 0;
        if (!chargeRequest.getDiscountCode().isEmpty()) {
            Optional<Float> discountOpt = discountService.findByCode(chargeRequest.getDiscountCode())
                    .map(discountEntity -> {
                        discountService.deleteDiscountByCode(chargeRequest.getDiscountCode());
                        return discountEntity.getDiscountValue() * event.getPrice();
                    });
            if (discountOpt.isPresent()) {
                discount += discountOpt.get();
            }
        }
        if (!chargeRequest.getReferralEmail().isEmpty()) {
            User user = userService.findByEmail(chargeRequest.getReferralEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referral user not found"));
            int discountForNextTicket = user.getUserDetails().getDiscountForNextTicket();
            discount += discountForNextTicket * event.getPrice();
            user.getUserDetails().setDiscountForNextTicket((int) (event.getDiscount() * chargeRequest.getTickets()));
            userService.save(user);
        }
        return discount;
    }

    private List<Ticket> generateTickets(ChargeRequest chargeRequest, Event event) {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < chargeRequest.getTickets(); i++) {
            Ticket ticket = new Ticket(UUID.randomUUID(), null, 0, "ticket", event);
            tickets.add(ticketService.saveTicket(ticket));
        }
        return tickets;
    }

    private void sendTicketsEmail(String userEmail, List<Ticket> tickets) {
        String emailBody = tickets.stream()
                .map(ticket -> "Ticket Code: " + ticket.getId().toString())
                .collect(Collectors.joining("\n"));
        emailSenderService.sendEmail(userEmail, "Your Tickets", emailBody);
    }

    private void updateEventStatistics(Event event, int ticketsSold, float moneyEarned) {
        Statistics statistics = statisticsService.getStatisticsByEventId(event.getId())
                .orElseGet(() -> {
                    Statistics newStatistics = new Statistics();
                    newStatistics.setEvent(event);
                    return newStatistics;
                });

        statistics.setTicketsSold(statistics.getTicketsSold() + ticketsSold);
        statistics.setMoneyEarned(statistics.getMoneyEarned().add(BigDecimal.valueOf(moneyEarned)));
        statisticsService.save(statistics);
}
}