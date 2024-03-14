package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.dto.EventStatisticsDTO;
import com.partyhub.PartyHub.dto.EventSummaryDto;
import com.partyhub.PartyHub.entities.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventService {
    Event addEvent(Event event);
    Event editEvent(UUID id, Event eventDetails);
    Optional<Event> getNearestEvent();
    Optional<Event> getEventById(UUID id);
    public List<EventSummaryDto> getAllEventSummaries();
    List<EventSummaryDto> getUpcomingEvents();
    Optional<EventStatisticsDTO> getEventStatisticsDTO(UUID eventId);

}

