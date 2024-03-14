package com.partyhub.PartyHub.mappers;

import com.partyhub.PartyHub.dto.EventDto;
import com.partyhub.PartyHub.dto.EventPhotoDto;
import com.partyhub.PartyHub.entities.Event;
import com.partyhub.PartyHub.entities.Ticket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventMapper {
    public EventDto eventToDto(Event event){
        return new EventDto(
                event.getId()
                ,event.getName()
                ,event.getMainBanner()
                ,event.getSecondaryBanner()
                ,event.getLocation()
                ,event.getCity()
                ,event.getLng()
                ,event.getLat()
                ,event.getDate()
                ,event.getDetails()
                ,event.getPrice()
                ,event.getDiscount()
                ,event.getTicketsNumber()
                ,event.getTicketsLeft());
    }
    public Event dtoToEvent(EventDto eventDto) {
        return Event.builder()
                .id(eventDto.getId())
                .name(eventDto.getName())
                .mainBanner(eventDto.getMainBanner())
                .secondaryBanner(eventDto.getSecondaryBanner())
                .location(eventDto.getLocation())
                .city(eventDto.getCity())
                .lng(eventDto.getLng())
                .lat(eventDto.getLat())
                .date(eventDto.getDate())
                .details(eventDto.getDetails())
                .price(eventDto.getPrice())
                .discount(eventDto.getDiscount())
                .ticketsNumber(eventDto.getTicketsNumber())
                .ticketsLeft(eventDto.getTicketsLeft())
                .build();
    }

    public EventPhotoDto eventToEventPhotoDto(Event event) {
        EventPhotoDto dto = new EventPhotoDto();
        dto.setId(event.getId());
        dto.setCity(event.getCity());
        dto.setMainBanner(event.getMainBanner());
        return dto;
    }


}
