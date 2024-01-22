package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/admin")
    public class AdminController {
        private final EventService eventService;

        @Autowired
        public AdminController(EventService eventService) {
            this.eventService = eventService;
        }

        @PostMapping("/events")
        public ResponseEntity<Event> addEvent(@RequestBody Event event) {
            try {
                Event savedEvent = eventService.addEvent(event);
                return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
}
