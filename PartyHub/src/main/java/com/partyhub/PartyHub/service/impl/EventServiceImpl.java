package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.dto.EventStatisticsDTO;
import com.partyhub.PartyHub.dto.EventSummaryDto;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.Statistics;
import com.partyhub.PartyHub.exceptions.EventNotFoundException;
import com.partyhub.PartyHub.repository.EventRepository;
import com.partyhub.PartyHub.repository.TicketRepository;
import com.partyhub.PartyHub.service.EventService;
import com.partyhub.PartyHub.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final StatisticsService statisticsService;
    private final TicketRepository ticketRepository;


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

    @Override
    public Event getNearestEvent(Optional<String> city) {
        LocalDate today = LocalDate.now();
        if(city.isPresent()) {
            return eventRepository.findTopByCityAndDateAfterOrderByDateAsc(city.get(), today)
                    .orElseThrow(() -> new EventNotFoundException("No upcoming events found in " + city.get()));
        } else {
            return eventRepository.findTopByDateAfterOrderByDateAsc(today)
                    .orElseThrow(() -> new EventNotFoundException("No upcoming events found"));
        }
    }

    @Override
    public Event getEventById(UUID id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Event not found!"));
    }

    @Override
    public List<EventSummaryDto> getAllEventSummaries() {
        return eventRepository.findAll().stream()
                .map(event -> new EventSummaryDto(event.getId(), event.getName(), event.getCity(), event.getDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventSummaryDto> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findByDateAfter(today).stream()
                .map(event -> new EventSummaryDto(event.getName(), event.getCity(), event.getDate()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EventStatisticsDTO> getEventStatisticsDTO(UUID eventId) {
        return eventRepository.findById(eventId)
                .map(event -> {
                    Statistics statistics = statisticsService.getStatisticsByEventId(eventId);
                    return new EventStatisticsDTO(
                            event.getName(),
                            event.getLocation(),
                            event.getDate(),
                            event.getPrice(),
                            event.getDiscount(),
                            statistics.getTicketsSold(),
                            statistics.getMoneyEarned(),
                            statistics.getGeneratedInvites(),
                            statistics.getTicketBasedAttendees(),
                            statistics.getInvitationBasedAttendees()
                    );
                });
    }

    @Override
    public void updateTicketsLeft(int tickets, Event event) {
        event.setTicketsLeft(event.getTicketsLeft() - tickets);
        this.addEvent(event);
    }

    @Override
    public boolean isSoldOut(UUID eventId) {
        Event event = this.getEventById(eventId);
        return event.getTicketsLeft() <= 0;
    }

    @Override
    public void deleteEventAndTicketsKeepStatistics(UUID eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            // Stergem biletele asociate evenimentului
            ticketRepository.deleteByEventId(eventId);
            // Stergem evenimentul, dar datorită setărilor de cascade, statisticile nu vor fi șterse
            eventRepository.deleteById(eventId);
        }
    }


}