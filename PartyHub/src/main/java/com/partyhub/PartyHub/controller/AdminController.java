package com.partyhub.PartyHub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyhub.PartyHub.dto.EventDto;
import com.partyhub.PartyHub.dto.EventSummaryDto;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.mappers.EventMapper;
import com.partyhub.PartyHub.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/admin")
public class AdminController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final ObjectMapper objectMapper;

    @PostMapping("/event")
    public ResponseEntity<Event> addEvent(@RequestParam("eventData") String eventDataJson,
                                          @RequestParam("mainBanner") MultipartFile mainBannerFile,
                                          @RequestParam("secondaryBanner") MultipartFile secondaryBannerFile) {
        try {
            EventDto eventDto = objectMapper.readValue(eventDataJson, EventDto.class);
            Event event = eventMapper.dtoToEvent(eventDto);

            byte[] mainBanner = processBannerFile(mainBannerFile);
            byte[] secondaryBanner = processBannerFile(secondaryBannerFile);

            event.setMainBanner(mainBanner);
            event.setSecondaryBanner(secondaryBanner);

            Event savedEvent = eventService.addEvent(event);
            return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private byte[] processBannerFile(MultipartFile bannerFile) {
        try {
            if (bannerFile != null && !bannerFile.isEmpty()) {
                return bannerFile.getBytes();
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    @PutMapping("event/{id}")
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

    @GetMapping("/events")
    public ResponseEntity<List<EventSummaryDto>> getAllEventSummaries() {
        List<EventSummaryDto> eventSummaries = eventService.getAllEventSummaries();
        return new ResponseEntity<>(eventSummaries, HttpStatus.OK);
    }
}
