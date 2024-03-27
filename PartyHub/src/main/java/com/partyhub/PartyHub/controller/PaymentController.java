package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.ChargeRequest;
import com.partyhub.PartyHub.dto.PaymentResponse;
import com.partyhub.PartyHub.entities.*;
import com.partyhub.PartyHub.exceptions.DiscountForNextTicketNotFoundException;
import com.partyhub.PartyHub.exceptions.DiscountNotFoundException;
import com.partyhub.PartyHub.exceptions.EventNotFoundException;
import com.partyhub.PartyHub.exceptions.UserNotFoundException;
import com.partyhub.PartyHub.service.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Flow;
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
    public ApiResponse chargeCard(@RequestBody ChargeRequest chargeRequest) {
        try {
            Stripe.apiKey = apiKey;

            Event event = eventService.getEventById(chargeRequest.getEventId());

            if(eventService.isSoldOud(chargeRequest.getEventId())){
                return new ApiResponse(false, "Tickets sold out!");
            }

            float discount = calculateDiscount(chargeRequest, event);
            System.out.println(discount);
            float price = (chargeRequest.getTickets() * event.getPrice()) * 100 - discount;
            System.out.println(price);

            ChargeCreateParams params = ChargeCreateParams.builder()
                    .setAmount((long) price)
                    .setCurrency("RON")
                    .setDescription("Payment for " + chargeRequest.getTickets() + " tickets to " + event.getName())
                    .setSource(chargeRequest.getToken())
                    .build();

            Charge charge = Charge.create(params);

            List<Ticket> tickets = generateTickets(chargeRequest, event);
            sendTicketsEmail(chargeRequest.getUserEmail(), tickets);

            eventService.updateTicketsLeft(chargeRequest.getTickets(), event);

            PaymentResponse paymentResponse = new PaymentResponse(charge.getId(), charge.getAmount(), charge.getCurrency(), charge.getDescription());
            return new ApiResponse(true, paymentResponse.toString());
        } catch (StripeException e) {
            return new ApiResponse(false, "Stripe error: " + e.getMessage());
        }catch (UserNotFoundException e) {
            return new ApiResponse(false, "User not found!: " + e.getMessage());
        }catch (EventNotFoundException e) {
            return new ApiResponse(false, "Event not found!: " + e.getMessage());
        } catch (Exception e) {
            return new ApiResponse(false, "An error occurred: " + e.getMessage());
        }
    }


    private float calculateDiscount(ChargeRequest chargeRequest, Event event) {
        float discount = 0f;

        try {
            User user = userService.findByEmail(chargeRequest.getUserEmail());
            DiscountForNextTicket discountForNextTicket = discountForNextTicketService.findDiscountForUserAndEvent(user.getUserDetails(), event);
            discount += discountForNextTicket.getValue() * chargeRequest.getTickets();
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
                        Ticket freeTicket = new Ticket(UUID.randomUUID(), LocalDateTime.now(), "FREE_TICKET", chargeRequest.getReferralEmail(), event);
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