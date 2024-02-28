package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.EventDto;
import com.partyhub.PartyHub.dto.EventPhotoDto;
import com.partyhub.PartyHub.entities.Discount;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.exceptions.EventNotFoundException;
import com.partyhub.PartyHub.mappers.EventMapper;
import com.partyhub.PartyHub.service.DiscountService;
import com.partyhub.PartyHub.service.EventService;
import com.partyhub.PartyHub.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Transactional
    @GetMapping("/event/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable UUID id) {
        try {
            Optional<Event> event = eventService.getEventById(id);
            if (event.isPresent()) {
                Event event1 = event.get();
                EventDto eventDto = eventMapper.eventToDto(event1);
                eventDto.setMainBanner(null);
                return new ResponseEntity<>(eventDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @GetMapping("/event")
    public ResponseEntity<EventPhotoDto> getNearestEventPhoto() {
        try {
            Event nearestEvent = eventService.getNearestEvent().orElseThrow(()-> new EventNotFoundException("Event not found!"));
            EventPhotoDto eventPhotoDto = eventMapper.eventToEventPhotoDto(nearestEvent);
            if (eventPhotoDto != null) {
                return new ResponseEntity<>(eventPhotoDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/check-promocode-or-discount")
    public ResponseEntity<?> checkPromoCodeOrDiscount(@RequestParam String code) {
        if (isValidPromoCode(code)) {
            Optional<User> userOptional = userService.findByPromoCode(code);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String fullName = user.getUserDetails().getFullName();
                return ResponseEntity.ok("Promo code belongs to user: " + fullName);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            Optional<Discount> discountOptional = discountService.findByCode(code);
            if (discountOptional.isPresent()) {
                Discount discount = discountOptional.get();
                return ResponseEntity.ok("Discount value: " + discount.getDiscountValue());
            } else {
                return ResponseEntity.notFound().build();
            }
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

}
