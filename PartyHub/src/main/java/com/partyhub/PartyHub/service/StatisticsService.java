package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.dto.EventStatisticsDTO;
import com.partyhub.PartyHub.entities.Statistics;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
public interface StatisticsService {
    Optional<Statistics> getStatisticsByEventId(UUID eventId);

}
