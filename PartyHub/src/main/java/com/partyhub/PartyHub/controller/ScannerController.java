package com.partyhub.PartyHub.controller;


import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.Statistics;
import com.partyhub.PartyHub.entities.Ticket;
import com.partyhub.PartyHub.service.StatisticsService;
import com.partyhub.PartyHub.service.TicketService;
import com.partyhub.PartyHub.util.TicketNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scan")
public class ScannerController {

    private final TicketService ticketService;
    private final StatisticsService statisticsService;

    @PostMapping("/validate/{ticketId}")
    public ResponseEntity<ApiResponse> validateTicket(@PathVariable UUID ticketId) {
        try {
            Ticket ticket = ticketService.findById(ticketId)
                    .orElseThrow(() -> new TicketNotFoundException("Ticket not found with ID: " + ticketId));

            if (ticket.getValidationDate() == null) {
                ticket.setValidationDate(LocalDateTime.now());
                Event event = ticket.getEvent();
                Statistics statistics = event.getStatistics();
                if (statistics == null) {
                    statistics = new Statistics();

                }

                if ("invite".equals(ticket.getType())) {
                    statistics.setInvitationBasedAttendees(statistics.getInvitationBasedAttendees() + 1);
                } else {
                    statistics.setTicketBasedAttendees(statistics.getTicketBasedAttendees() + 1);
                }
                statisticsService.save(statistics);

                ticketService.saveTicket(ticket);
                return ResponseEntity.ok(new ApiResponse(true, "Ticket has been successfully validated."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Ticket has already been used and cannot be validated again."));
            }
        } catch (TicketNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "An error occurred while validating the ticket."));
        }
    }
}
