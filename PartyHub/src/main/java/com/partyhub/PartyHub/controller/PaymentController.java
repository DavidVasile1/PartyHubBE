package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.ChargeRequest;
import com.partyhub.PartyHub.dto.PaymentResponse;
import com.partyhub.PartyHub.entities.*;
import com.partyhub.PartyHub.service.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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
    private final DiscountForNextTicketService discountForNextTicketService;


    @Value("${stripe.keys.secret}")
    private String apiKey;


    @PostMapping("/charge")
    public ApiResponse chargeCard(@RequestBody ChargeRequest chargeRequest) throws StripeException {

        Stripe.apiKey = apiKey;

        Event event = eventService.getEventById(chargeRequest.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        if(event.getTicketsLeft() <= 0){
            return new ApiResponse(false, "Tickets sold out!");
        }

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

        this.eventService.updateTicketsLeft(chargeRequest.getTickets(), event);



        PaymentResponse paymentResponse =  new PaymentResponse(charge.getId(), charge.getAmount(), charge.getCurrency(), charge.getDescription());
        return new ApiResponse(true,paymentResponse.toString() );
    }

    private float calculateDiscount(ChargeRequest chargeRequest, Event event) {
        AtomicReference<Float> discount = new AtomicReference<>(0f);

        if (!chargeRequest.getDiscountCode().isEmpty()) {
            discountService.findByCode(chargeRequest.getDiscountCode())
                    .map(discountEntity -> {
                        discountService.deleteDiscountByCode(chargeRequest.getDiscountCode());
                        return discountEntity.getDiscountValue() * event.getPrice() * chargeRequest.getTickets();
                    })
                    .ifPresent(discountValue -> discount.updateAndGet(v -> v + discountValue));
        }

        if (!chargeRequest.getReferralEmail().isEmpty()) {
            userService.findByEmail(chargeRequest.getReferralEmail())
                    .map(User::getUserDetails) // Assuming a method or mapping exists
                    .flatMap(userDetails -> discountForNextTicketService.findDiscountForUserAndEvent(userDetails, event))
                    .ifPresent(discountForNextTicket -> {
                        float updatedDiscount = discount.get() + discountForNextTicket.getValue() * chargeRequest.getTickets();
                        discount.set(updatedDiscount);
                        discountForNextTicketService.useDiscountForNextTicket(discountForNextTicket);
                    });
        }

        return discount.get();
    }


    private List<Ticket> generateTickets(ChargeRequest chargeRequest, Event event) {
        List<Ticket> tickets = new ArrayList<>();
        String userEmail = chargeRequest.getUserEmail();
        for (int i = 0; i < chargeRequest.getTickets(); i++) {
            Ticket ticket = new Ticket(UUID.randomUUID(), null, "ticket",userEmail,event);
            tickets.add(ticketService.saveTicket(ticket));
        }
        return tickets;
    }

    private void sendTicketsEmail(String userEmail, List<Ticket> tickets) {
        StringBuilder emailContent = new StringBuilder("<h1>Your Tickets</h1>");

        for (Ticket ticket : tickets) {
            String qrCodeData = ticket.getId().toString();
            String qrCodeImageBase64 = generateQRCodeImageBase64(qrCodeData, 200, 200); // Generate QR code

            emailContent.append("<div style='margin-bottom: 20px;'>") // Added margin for better spacing
                    .append("<p>Your ticket QR code:</p>")
                    .append("<img src=\"data:image/png;base64,")
                    .append(qrCodeImageBase64)
                    .append("\" alt='Ticket QR Code' style='border: 1px solid #ddd; border-radius: 4px; padding: 5px; width: 150px;' /></div>");
        }

        emailSenderService.sendEmail(userEmail, "Your Tickets", emailContent.toString());
    }

    private String generateQRCodeImageBase64(String data, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
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