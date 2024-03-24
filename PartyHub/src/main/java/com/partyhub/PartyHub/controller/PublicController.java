package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.EventDto;
import com.partyhub.PartyHub.dto.EventPhotoDto;
import com.partyhub.PartyHub.dto.EventTicketInfoDTO;
import com.partyhub.PartyHub.entities.Discount;
import com.partyhub.PartyHub.entities.DiscountForNextTicket;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.exceptions.DiscountForNextTicketNotFoundException;
import com.partyhub.PartyHub.exceptions.EventNotFoundException;
import com.partyhub.PartyHub.mappers.EventMapper;
import com.partyhub.PartyHub.service.DiscountForNextTicketService;
import com.partyhub.PartyHub.service.DiscountService;
import com.partyhub.PartyHub.service.EventService;
import com.partyhub.PartyHub.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/public")
public class PublicController {


    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final DiscountService discountService;
    private final DiscountForNextTicketService discountForNextTicketService;

    @Transactional
    @GetMapping("/event/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable UUID id) {
        try {
            Event event = eventService.getEventById(id);
            EventDto eventDto = eventMapper.eventToDto(event);
            eventDto.setMainBanner(null);
            return new ResponseEntity<>(eventDto, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @GetMapping("/event")
    public ResponseEntity<EventPhotoDto> getNearestEventPhoto() {
        try {
            Event nearestEvent = eventService.getNearestEvent();
            EventPhotoDto eventPhotoDto = eventMapper.eventToEventPhotoDto(nearestEvent);
            if (eventPhotoDto != null) {
                return new ResponseEntity<>(eventPhotoDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/apply-promocode-or-discount")
    public ResponseEntity<ApiResponse> checkPromoCodeOrDiscount(@RequestParam String code) {
        try {
            if (code.length() == 9) {
                if (isValidPromoCode(code)) {
                    User user = userService.findByPromoCode(code);

                        String email = user.getEmail();
                        return new ResponseEntity<>(new ApiResponse(true, email), HttpStatus.OK);
                }
            } else if (code.length() == 10) {
                try {
                    Discount discount = discountService.findByCode(code);
                    return new ResponseEntity<>(new ApiResponse(true, String.valueOf(discount.getDiscountValue())), HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>(new ApiResponse(false, "Discount code not found"), HttpStatus.NOT_FOUND);
                }
            }

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ApiResponse(false, "Not a valid form"), HttpStatus.NOT_FOUND);
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
    @GetMapping("/event-price/{id}")
    public ResponseEntity<BigDecimal> getEventPrice(@PathVariable UUID id) {
        try {
            Event event = eventService.getEventById(id);
            BigDecimal price = BigDecimal.valueOf(event.getPrice());
            return new ResponseEntity<>(price, HttpStatus.OK);
        } catch (EventNotFoundException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/event-payment-details/{id}")
    public ResponseEntity<EventTicketInfoDTO> getEventTicketInfo(@PathVariable UUID id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            Event event = eventService.getEventById(id);

            int discountForNextTicket = 0;
            try {
                discountForNextTicket = discountForNextTicketService.findDiscountForUserAndEvent(user.getUserDetails(), event).getValue();
            } catch (DiscountForNextTicketNotFoundException e) {
                discountForNextTicketService.addOrUpdateDiscountForUser(user, event, 0);
            }

            EventTicketInfoDTO eventTicketInfoDTO = new EventTicketInfoDTO(
                    BigDecimal.valueOf(event.getPrice()),
                    event.getDiscount(),
                    event.getTicketsLeft(),
                    event.getTicketsNumber(),
                    discountForNextTicket
            );
            return ResponseEntity.ok(eventTicketInfoDTO);
        }catch (EventNotFoundException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
