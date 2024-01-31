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
    public Event dtoToEvent(EventDto eventDto){
        List<Ticket> list = new ArrayList<Ticket>();
        return new Event(
                eventDto.getId()
                ,eventDto.getName()
                ,eventDto.getMainBanner()
                ,eventDto.getSecondaryBanner()
                ,eventDto.getLocation()
                ,eventDto.getCity()
                ,eventDto.getLng()
                ,eventDto.getLat()
                ,eventDto.getDate()
                ,eventDto.getDetails()
                ,eventDto.getPrice()
                ,eventDto.getDiscount()
                ,eventDto.getTicketsNumber()
                ,eventDto.getTicketsLeft()
                ,list
                );
    }

    public EventPhotoDto eventToEventPhotoDto(Event event) {
        EventPhotoDto dto = new EventPhotoDto();
        dto.setId(event.getId());
        dto.setCity(event.getCity());
        dto.setMainBanner(event.getMainBanner());
        return dto;
    }

}
