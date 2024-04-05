package com.partyhub.PartyHub.util;

import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.repository.EventRepository;
import com.partyhub.PartyHub.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Component
@AllArgsConstructor
public class EventCleanupScheduler {
    private final EventRepository eventRepository;
    private final EventService eventService;

    @Scheduled(cron = "0 0 * * * ?")
    public void checkAndDeletePastEvents() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now(ZoneId.systemDefault());
        List<Event> events = eventRepository.findAll();
        for(Event event : events) {
            if (event.getDate().plusDays(1).isEqual(today) && now.isAfter(LocalTime.of(5, 0))) {
                eventService.deleteEventAndTicketsKeepStatistics(event.getId());
            }
        }
    }
}
