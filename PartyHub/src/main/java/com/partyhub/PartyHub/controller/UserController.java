package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.EventDto;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.exceptions.UserNotFoundException;
import com.partyhub.PartyHub.mappers.EventMapper;
import com.partyhub.PartyHub.service.EventService;
import com.partyhub.PartyHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EventService eventService;
    private final EventMapper eventMapper;


    @GetMapping("/event")
    public ResponseEntity<EventDto> getNearestEvent() {
        try {
            Event nearestEvent = eventService.getNearestEvent();
            EventDto eventDto = eventMapper.eventToDto(nearestEvent);
            if (eventDto != null) {
                return new ResponseEntity<>(eventDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("generate-promo-code")
    public ResponseEntity<String> generatePromoCode() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user= userService.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not found!"));
            if (user != null) {
                user.generatePromoCode();
                userService.save(user);
                return new ResponseEntity<>(user.getPromoCode(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
