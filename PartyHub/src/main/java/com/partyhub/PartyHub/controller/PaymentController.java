package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.ChargeRequest;
import com.partyhub.PartyHub.entities.*;
import com.partyhub.PartyHub.exceptions.EventNotFoundException;
import com.partyhub.PartyHub.service.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentController {

    private final EventService eventService;
    private final TicketService ticketService;
    private final DiscountService discountService;
    private final UserService userService;
    private final StatisticsService statisticsService;
    private final DiscountForNextTicketService discountForNextTicketService;


    @Value("${stripe.keys.secret}")
    private String apiKey;


    @PostMapping("/charge")
    public ApiResponse chargeCard(@RequestBody ChargeRequest chargeRequest) {
        Event event = null;
        float price = 0;
        try {
            event = eventService.getEventById(chargeRequest.getEventId());

            if (eventService.isSoldOut(chargeRequest.getEventId())) {
                return new ApiResponse(false, "Biletele au fost vândute.");
            }

            float discount = calculateDiscount(chargeRequest, event);
            price = (chargeRequest.getTickets() * event.getPrice()) * 100 - discount;
            List<Ticket> tickets = generateTickets(chargeRequest, event);
            if (price > 0) {
                Stripe.apiKey = apiKey;
                ChargeCreateParams params = ChargeCreateParams.builder()
                        .setAmount((long) price)
                        .setCurrency("RON")
                        .setDescription("Plată pentru " + chargeRequest.getTickets() + " bilete la " + event.getName())
                        .setSource(chargeRequest.getToken())
                        .build();

                Charge.create(params);

                sendTicketsEmail(chargeRequest.getUserEmail(), tickets,event.getSecondaryBanner());
                return new ApiResponse(true, "Plata a fost efectuată cu succes. Biletele au fost trimise.");
            } else {
                sendTicketsEmail(chargeRequest.getUserEmail(), tickets,event.getSecondaryBanner());
                return new ApiResponse(true, "Biletele au fost emise cu succes, fără plată necesară.");
            }
        } catch (StripeException e) {
            return new ApiResponse(false, "Eroare Stripe: " + e.getMessage());
        } catch (EventNotFoundException e) {
            return new ApiResponse(false, "Evenimentul nu a fost găsit: " + e.getMessage());
        } catch (Exception e) {
            return new ApiResponse(false, "A apărut o eroare: " + e.getMessage());
        } finally {
            if (event != null) {
                float finalPrice = Math.max(0, price);
                updateEventStatistics(event, chargeRequest.getTickets(), finalPrice);
                eventService.updateTicketsLeft(chargeRequest.getTickets(), event);
            }
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
                discountForNextTicketService.addOrUpdateDiscountForUser(referrerUser, event, event.getDiscount()*chargeRequest.getTickets());
                discount += event.getDiscount()*chargeRequest.getTickets()*event.getPrice();
                DiscountForNextTicket discountForNextTicket = discountForNextTicketService.findDiscountForUserAndEvent(referrerUser.getUserDetails(),event);
                int freeTicketsCount = discountForNextTicket.getValue() / 100;
                if (freeTicketsCount > 0) {
                    List<Ticket> freeTickets = new ArrayList<>();
                    for (int i = 0; i < freeTicketsCount; i++) {
                        Ticket freeTicket = new Ticket(UUID.randomUUID(), null, "FREE_TICKET", chargeRequest.getReferralEmail(), event);
                        freeTickets.add(ticketService.saveTicket(freeTicket));
                    }
                    sendTicketsEmail(chargeRequest.getReferralEmail(), freeTickets,event.getSecondaryBanner());
                }
                System.out.println("the discount for next ticket of referral user is increased by " + (event.getDiscount() * chargeRequest.getTickets()));
                System.out.println("the discount using promocode is " + discount);
                discountForNextTicket.setValue(discountForNextTicket.getValue()%100);
                discountForNextTicketService.saveDiscountForNextTicket(discountForNextTicket);

            } catch (Exception e) {
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

    private void sendTicketsEmail(String userEmail, List<Ticket> tickets, byte[] secondaryBannerImage) {
        try {
            // Email configuration properties
            String host = "smtp.gmail.com";
            int port = 587;
            String username = "party.hub.00@gmail.com"; // Your email
            String password = "aqrfoyixtzdsiazo"; // Your password or app-specific password

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            // Create a mail session
            Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // Construct the email message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Your Tickets");

            // Create the multipart content
            MimeMultipart multipart = new MimeMultipart("related");

            // Add the secondary banner part
            MimeBodyPart bannerPart = new MimeBodyPart();
            DataSource fdsBanner = new ByteArrayDataSource(secondaryBannerImage, "image/png");
            bannerPart.setDataHandler(new DataHandler(fdsBanner));
            bannerPart.setHeader("Content-ID", "<secondaryBanner>");
            multipart.addBodyPart(bannerPart);

            // Generate and add a QR code for each ticket
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                BitMatrix bitMatrix = qrCodeWriter.encode(ticket.getId().toString(), BarcodeFormat.QR_CODE, 350, 350);
                ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
                byte[] qrCodeImage = pngOutputStream.toByteArray();

                // Create the image part with a unique Content-ID for each ticket
                MimeBodyPart qrPart = new MimeBodyPart();
                DataSource fdsQR = new ByteArrayDataSource(qrCodeImage, "image/png");
                qrPart.setDataHandler(new DataHandler(fdsQR));
                qrPart.setHeader("Content-ID", "<qrCodeImage" + i + ">");
                multipart.addBodyPart(qrPart);
            }

            // Create the HTML content with placeholders for the secondary banner and QR codes
            StringBuilder emailContentBuilder = new StringBuilder();
            emailContentBuilder.append("<html><body>");
            emailContentBuilder.append("<img src='cid:secondaryBanner' alt='Secondary Banner' style='width:100%; height: auto;'><br>");

            Event event = tickets.get(0).getEvent();
            for (int i = 0; i < tickets.size(); i++) {
                emailContentBuilder.append("<h1>").append(event.getName()).append("</h1>");
                emailContentBuilder.append("<p>").append(event.getLocation()).append(", ").append(event.getCity()).append("</p>");
                emailContentBuilder.append("<p>Numărul de serie al biletului: ").append(tickets.get(i).getId().toString()).append("</p>");
                emailContentBuilder.append("<p>Acces între: ").append(formatDate(event.getDate().toString())).append(" 22:00 și ").append(formatDate(event.getDate().toString())).append(" 05:00</p>");
                emailContentBuilder.append("<p>Valid pentru o singură persoană</p>");
                emailContentBuilder.append("<img src='cid:qrCodeImage").append(i).append("' alt='QR Code' style='width:150px; height:150px;'><br>");
                emailContentBuilder.append("<p>Organizator: Mamara Catalin Daniel, 49699573</p><br><br>");
            }
            emailContentBuilder.append("</body></html>");

            // Create the message body part
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(emailContentBuilder.toString(), "text/html");
            multipart.addBodyPart(htmlPart);

            // Set the multipart as the content of the message
            message.setContent(multipart);

            // Send the email
            Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private void updateEventStatistics(Event event, int ticketsSold, float moneyEarned) {
        Statistics statistics = statisticsService.getStatisticsByEventId(event.getId());

        statistics.setTicketsSold(statistics.getTicketsSold() + ticketsSold);
        statistics.setMoneyEarned(statistics.getMoneyEarned().add(BigDecimal.valueOf(moneyEarned).divide(BigDecimal.valueOf(100))));
        statisticsService.save(statistics);
    }

    private byte[] generateQRCodeImage(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 350, 350);
        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        }

    }

    private String formatDate(String dateString){
       String[] dateParts = dateString.split("-");

        String day = dateParts[2];
        String month = dateParts[1];

        return day + "." + month;
    }

}