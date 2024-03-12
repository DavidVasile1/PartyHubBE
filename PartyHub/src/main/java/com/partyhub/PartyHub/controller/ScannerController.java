package com.partyhub.PartyHub.controller;


import com.partyhub.PartyHub.entities.Ticket;
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

    @PostMapping("/validate/{ticketId}")
    public ResponseEntity<ApiResponse> validateTicket(@PathVariable UUID ticketId) {
        try {
            Ticket ticket = ticketService.findById(ticketId)
                    .orElseThrow(() -> new TicketNotFoundException("Ticket not found with ID: " + ticketId));

            if (ticket.getValidationDate() == null) {
                // Ticket is valid because validationDate is null. Now set the validationDate to current datetime to mark it as used/validated.
                ticket.setValidationDate(LocalDateTime.now());
                ticketService.saveTicket(ticket); // Save the updated ticket
                return ResponseEntity.ok(new ApiResponse(true, "Ticket has been successfully validated."));
            } else {
                // If the ticket's validationDate is not null, it indicates the ticket has already been validated/used.
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Ticket has already been used and cannot be validated again."));
            }
        } catch (TicketNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "An error occurred while validating the ticket."));
        }
    }
}
