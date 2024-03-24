package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.EventDto;
import com.partyhub.PartyHub.dto.EventPhotoDto;
import com.partyhub.PartyHub.dto.EventTicketInfoDTO;
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

    @PostMapping("/apply-promocode-or-discount")
    public ResponseEntity<ApiResponse> checkPromoCodeOrDiscount(@RequestParam String code) {
        if(code.length() == 9){
            if (isValidPromoCode(code)) {
                Optional<User> userOptional = userService.findByPromoCode(code);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    String email = user.getEmail();
                    return new ResponseEntity<>(new ApiResponse(true, email), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new ApiResponse(false, "Not a valid promocode"), HttpStatus.NOT_FOUND);
                }
            }
        }else{
            if (code.length() == 10){
            Optional<Discount> discountOptional = discountService.findByCode(code);
                if (discountOptional.isPresent()) {
                    Discount discount = discountOptional.get();
                    return new ResponseEntity<>( new ApiResponse(true, String.valueOf(discount.getDiscountValue())), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new ApiResponse(false, "Not a valid discount"), HttpStatus.NOT_FOUND);
                }
            }
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
        try{
            Event event = eventService.getEventById(id);
            EventTicketInfoDTO eventTicketInfoDTO = new EventTicketInfoDTO(
                BigDecimal.valueOf(event.getPrice()),
                event.getDiscount(),
                event.getTicketsLeft(),
                event.getTicketsNumber()

            );
            return ResponseEntity.ok(eventTicketInfoDTO);
        }catch (EventNotFoundException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }


    }

}
