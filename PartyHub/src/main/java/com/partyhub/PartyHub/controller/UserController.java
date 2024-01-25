package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final EventService eventService;

    @Autowired
    public UserController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/nearest-event")
    public ResponseEntity<Event> getNearestEvent() {
        try {
            Event nearestEvent = eventService.getNearestEvent();
            if (nearestEvent != null) {
                return new ResponseEntity<>(nearestEvent, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
