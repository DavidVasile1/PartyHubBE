package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.repository.EventRepository;
import com.partyhub.PartyHub.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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

    @Override
    public Event editEvent(UUID id, Event eventDetails) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isPresent()) {
            Event existingEvent = optionalEvent.get();

            existingEvent.setName(eventDetails.getName());
            existingEvent.setMainBanner(eventDetails.getMainBanner());
            existingEvent.setSecondaryBanner(eventDetails.getSecondaryBanner());
            existingEvent.setLocation(eventDetails.getLocation());
            existingEvent.setDate(eventDetails.getDate());
            existingEvent.setDetails(eventDetails.getDetails());
            existingEvent.setPrice(eventDetails.getPrice());
            existingEvent.setDiscount(eventDetails.getDiscount());
            existingEvent.setTicketsNumber(eventDetails.getTicketsNumber());

            return eventRepository.save(existingEvent);
        } else {
            throw new RuntimeException("Event not found for this id :: " + id);
        }
    }
}
