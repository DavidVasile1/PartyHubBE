package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.EventDto;
import com.partyhub.PartyHub.dto.EventPhotoDto;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.mappers.EventMapper;
import com.partyhub.PartyHub.service.EventService;
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

    @Transactional
    @GetMapping("/event/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable UUID id) {
        try {
            Optional<Event> event = eventService.getEventById(id);
            if (event.isPresent()) {
                Event event1 = event.get();
                EventDto eventDto = eventMapper.eventToDto(event1);
                return new ResponseEntity<>(eventDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


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
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
