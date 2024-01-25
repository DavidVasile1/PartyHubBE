package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.EventDto;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.mappers.EventMapper;
import com.partyhub.PartyHub.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
        private final EventService eventService;
        private final EventMapper eventMapper;

        @PostMapping("/event")
        public ResponseEntity<Event> addEvent(@RequestBody EventDto eventDto)   {
            try {
                Event event = eventMapper.dtoToEvent(eventDto);
                Event savedEvent = eventService.addEvent(event);
                return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @PutMapping("/event/{id}")
        public ResponseEntity<Event> editEvent(@PathVariable UUID id, @RequestBody EventDto eventDto) {
            try {
                Event event = eventMapper.dtoToEvent(eventDto);
                Event updatedEvent = eventService.editEvent(id, event);
                if (updatedEvent != null) {
                    return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }


}
