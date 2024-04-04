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
import java.util.UUID;

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
    public ApiResponse chargeCard(@RequestBody ChargeRequest chargeRequest) {
        try {
            Event event = eventService.getEventById(chargeRequest.getEventId());

            if(eventService.isSoldOut(chargeRequest.getEventId())){
                return new ApiResponse(false, "Biletele au fost vândute.");
            }

            float discount = calculateDiscount(chargeRequest, event);
            float price = (chargeRequest.getTickets() * event.getPrice()) * 100 - discount;

            // Verifică dacă prețul final este 0
            if (price <= 0) {
                // Procesul de generare a biletelor și trimiterea prin email
                List<Ticket> tickets = generateTickets(chargeRequest, event);
                sendTicketsEmail(chargeRequest.getUserEmail(), tickets);

                // Actualizează statisticile și numărul de bilete rămase fără o tranzacție Stripe
                updateEventStatistics(event, chargeRequest.getTickets(), 0);

                // Actualizează numărul de bilete rămase pentru eveniment
                eventService.updateTicketsLeft(chargeRequest.getTickets(), event);

                return new ApiResponse(true, "Biletele au fost emise cu succes, fără plată necesară.");
            }

            Stripe.apiKey = apiKey;

            ChargeCreateParams params = ChargeCreateParams.builder()
                    .setAmount((long) price)
                    .setCurrency("RON")
                    .setDescription("Plată pentru " + chargeRequest.getTickets() + " bilete la " + event.getName())
                    .setSource(chargeRequest.getToken())
                    .build();

            Charge charge = Charge.create(params);

            // Restul procesului de generare a biletelor, trimiterea prin email și actualizare statistici
            List<Ticket> tickets = generateTickets(chargeRequest, event);
            sendTicketsEmail(chargeRequest.getUserEmail(), tickets);

            eventService.updateTicketsLeft(chargeRequest.getTickets(), event);

            PaymentResponse paymentResponse = new PaymentResponse(charge.getId(), charge.getAmount(), charge.getCurrency(), charge.getDescription());
            return new ApiResponse(true, paymentResponse.toString());
        } catch (StripeException e) {
            return new ApiResponse(false, "Eroare Stripe: " + e.getMessage());
        } catch (Exception e) {
            return new ApiResponse(false, "A apărut o eroare: " + e.getMessage());
        }
    }


    private float calculateDiscount(ChargeRequest chargeRequest, Event event) {
        float discount = 0f;

        try {
            User user = userService.findByEmail(chargeRequest.getUserEmail());
            DiscountForNextTicket discountForNextTicket = discountForNextTicketService.findDiscountForUserAndEvent(user.getUserDetails(), event);
            discount += discountForNextTicket.getValue() * event.getPrice();
            discountForNextTicketService.useDiscountForNextTicket(discountForNextTicket);
            System.out.println("discount after applying discount for next ticket" + discount);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        if (!chargeRequest.getDiscountCode().isEmpty()) {
            try {
                Discount discountEntity = discountService.findByCode(chargeRequest.getDiscountCode());
                discount += discountEntity.getDiscountValue() * event.getPrice();
                discountService.deleteDiscountByCode(chargeRequest.getDiscountCode());
                System.out.println("discount using discount code" + discount);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }

        if (!chargeRequest.getReferralEmail().isEmpty()) {
            try {
                User referrerUser = userService.findByEmail(chargeRequest.getReferralEmail());
                discountForNextTicketService.addOrUpdateDiscountForUser(referrerUser, event, (int) (event.getDiscount()*chargeRequest.getTickets()));
                discount += event.getDiscount()*chargeRequest.getTickets()*event.getPrice();
                DiscountForNextTicket discountForNextTicket = discountForNextTicketService.findDiscountForUserAndEvent(referrerUser.getUserDetails(),event);
                int freeTicketsCount = (int) discountForNextTicket.getValue() / 100;
                if (freeTicketsCount > 0) {
                    List<Ticket> freeTickets = new ArrayList<>();
                    for (int i = 0; i < freeTicketsCount; i++) {
                        Ticket freeTicket = new Ticket(UUID.randomUUID(), null, "FREE_TICKET", chargeRequest.getReferralEmail(), event);
                        freeTickets.add(ticketService.saveTicket(freeTicket));
                    }
                    sendTicketsEmail(chargeRequest.getReferralEmail(), freeTickets);
                }
                System.out.println("the discount for next ticket of referal user is increased by " + (int) (event.getDiscount()*chargeRequest.getTickets()));
                System.out.println("the discount using promocode is " + discount);
                discountForNextTicket.setValue(discountForNextTicket.getValue()%100);
                discountForNextTicketService.saveDiscountForNextTicket(discountForNextTicket);

            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }

        return discount;
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
        Statistics statistics = statisticsService.getStatisticsByEventId(event.getId());

        statistics.setTicketsSold(statistics.getTicketsSold() + ticketsSold);
        statistics.setMoneyEarned(statistics.getMoneyEarned().add(BigDecimal.valueOf(moneyEarned)));
        statisticsService.save(statistics);
    }
}