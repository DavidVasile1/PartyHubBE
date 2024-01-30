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
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/admin")
public class AdminController {
        private final EventService eventService;
        private final EventMapper eventMapper;

        @PostMapping("/event")
        public ResponseEntity<ApiResponse> addEvent(@RequestBody EventDto eventDto)   {
            try {
                Event event = eventMapper.dtoToEvent(eventDto);
                eventService.addEvent(event);
                return new ResponseEntity<>(new ApiResponse(true, "Event created!"), HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(new ApiResponse(false, "Event not created!"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @PutMapping("/event/{id}")
        public ResponseEntity<ApiResponse> editEvent(@PathVariable UUID id, @RequestBody EventDto eventDto) {
            try {
                Event event = eventMapper.dtoToEvent(eventDto);
                Event updatedEvent = eventService.editEvent(id, event);
                if (updatedEvent != null) {
                    return new ResponseEntity<>(new ApiResponse(true, "Event updated!"), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new ApiResponse(false, "Event not found!"), HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(new ApiResponse(false, "Event not updated!"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }


}
