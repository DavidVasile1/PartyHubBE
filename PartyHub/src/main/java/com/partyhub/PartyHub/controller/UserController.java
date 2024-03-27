package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.TicketDTO;
import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.exceptions.UserNotFoundException;
import com.partyhub.PartyHub.service.TicketService;
import com.partyhub.PartyHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final TicketService ticketService;

    @GetMapping("/promo-code")
    public ResponseEntity<ApiResponse> getPromoCode() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (user.getPromoCode() == null || user.getPromoCode().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(false, "Promo code does not exists") , HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(new ApiResponse(true, user.getPromoCode()) , HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/generate-promo-code")
    public ResponseEntity<ApiResponse> generatePromoCode() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (user.getPromoCode() == null || user.getPromoCode().isEmpty()) {
                userService.generateAndSetPromoCodeForUser(user.getId());
                return new ResponseEntity<>(new ApiResponse(true, user.getPromoCode()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse(false, "Promo code already exists") , HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/promo-code")
    public ResponseEntity<ApiResponse> editPromoCode(@RequestBody String newPromoCode) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.findByEmail(email);

            if (!isValidPromoCode(newPromoCode)) {
                return new ResponseEntity<>(new ApiResponse(false, "Invalid promo code format"), HttpStatus.BAD_REQUEST);
            }

            if (userService.isPromoCodeInUse(newPromoCode)) {
                return new ResponseEntity<>(new ApiResponse(false, "Promo code already in use"), HttpStatus.BAD_REQUEST);
            }

            user.setPromoCode(newPromoCode);
            userService.save(user);
            return new ResponseEntity<>(new ApiResponse(true, "Promo code updated successfully"), HttpStatus.OK);

        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse(false, "User not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidPromoCode(String promoCode) {
        if (promoCode == null || promoCode.length() != 9) {
            return false;
        }

        for (int i = 0; i < promoCode.length(); i++) {
            char ch = promoCode.charAt(i);
            if (!Character.isLowerCase(ch) && !Character.isDigit(ch)) {
                return false;
            }
        }

        return true;
    }

    @GetMapping("/check-promo-code")
    public ResponseEntity<String> checkPromoCode(@RequestParam String promoCode) {
        boolean exists = userService.doesPromoCodeExist(promoCode);
        if (exists) {
            return new ResponseEntity<>("Promo code exists", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Promo code does not exist", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketDTO>> getTicketsByEmail(@RequestParam String email) {
        try {
            List<TicketDTO> tickets = ticketService.getAllTicketsByEmail(email);
            if (tickets != null && !tickets.isEmpty()) {
                return new ResponseEntity<>(tickets, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
