package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.repository.EventRepository;
import com.partyhub.PartyHub.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event addEvent(Event event) {
        return eventRepository.save(event);
    }
}
