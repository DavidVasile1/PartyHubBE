package com.partyhub.PartyHub.controller;
import com.google.gson.Gson;
import com.partyhub.PartyHub.entities.CheckoutPayment;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.Ticket;
import com.partyhub.PartyHub.exceptions.EventNotFoundException;
import com.partyhub.PartyHub.service.*;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.checkout.Session;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.Stripe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class StripeController {

    static {
        init(); // This will set the Stripe API key as soon as the class is loaded
    }

    private final DiscountService discountService;
    private final UserService userService;
    private final TicketService ticketService;
    private final EmailSenderService emailSenderService;
    private final EventService eventService;
    private static Gson gson = new Gson();

    private static void init() {
        Stripe.apiKey = "sk_test_51OjlkICogk4f4bM9U9BxTLIL2PPn0PBs2PA8AGnMcphk7BuqrCdD87c0XegnKMeK4WnayiL0O1vstsS9cwpTPkNb00XaZ8fJIo";
    }


    @PostMapping("/payment")
    public String paymentWithCheckoutPage(@RequestBody CheckoutPayment payment) throws StripeException {
        Event event = eventService.getEventById(payment.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found for id: " + payment.getEventId()));
        long eventPrice = Math.round(event.getPrice() * 100);
        long discountAmount = calculateDiscountAmount(payment, event);
        long totalAmount = calculateTotalAmount(eventPrice, payment.getQuantity(), discountAmount);
        Charge session = createStripeSession(payment, totalAmount, event);
        processPostPaymentActions(payment, totalAmount, event);
        return gson.toJson(Map.of("id", session.getId()));
    }

    private long calculateDiscountAmount(CheckoutPayment payment, Event event) {
        if (!payment.getReferalEmail().isEmpty() && !payment.getDiscountCode().isEmpty()) {
            return discountService.findByCode(payment.getDiscountCode())
                    .map(discount -> Math.round(discount.getDiscountValue() * 100))
                    .orElse(Math.round(event.getDiscount() * 100));
        }
        return Math.round(event.getDiscount() * 100);
    }

    private long calculateTotalAmount(long eventPrice, int quantity, long discountAmount) {
        long totalAmount = eventPrice * quantity - discountAmount;
        return Math.max(totalAmount, 0);
    }

    private Charge createStripeSession(CheckoutPayment payment, long totalAmount, Event event) throws StripeException {
        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount((long) (totalAmount * 100)) // Stripe expects amount in cents
                .setCurrency("ron")
                .setSource(payment.getToken())
                .setDescription("Description for your charge")
                .build();

        return Charge.create(params);
    }

    private void processPostPaymentActions(CheckoutPayment payment, long totalAmount, Event event) {
        float pricePaid = totalAmount / 100.0f;

        LocalDateTime now = LocalDateTime.now();

        Ticket ticket = ticketService.generateAndSaveTicketForEvent(pricePaid, "Standard", payment.getEventId(), now);
        String emailBody = constructEmailBody(ticket, event, pricePaid);
        emailSenderService.sendEmail(payment.getUserEmail(), "Your Ticket Confirmation", emailBody);

        if (!payment.getDiscountCode().isEmpty()) {
            discountService.deleteDiscountByCode(payment.getDiscountCode());
        }
        if (!payment.getReferalEmail().isEmpty()) {
            userService.increaseDiscountForNextTicket(payment.getReferalEmail(), payment.getEventId());
        }
    }

    private String constructEmailBody(Ticket ticket, Event event, float pricePaid) {
        return String.format(
                "Thank you for your purchase! Your ticket for %s is confirmed.\n\nTicket details:\n- Event: %s\n- Date: %s\n- Location: %s\n- Price: $%.2f\n\nWe look forward to seeing you at the event!",
                event.getName(), event.getName(), event.getDate(), event.getLocation(), pricePaid
        );
    }


}

