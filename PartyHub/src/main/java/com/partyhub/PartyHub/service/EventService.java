package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.Event;
import java.util.UUID;

public interface EventService {
        Event addEvent(Event event);
        Event editEvent(UUID id, Event eventDetails);
        Event getNearestEvent();
    }

