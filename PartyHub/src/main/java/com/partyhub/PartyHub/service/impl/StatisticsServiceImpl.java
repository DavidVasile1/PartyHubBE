package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.entities.Statistics;
import com.partyhub.PartyHub.exceptions.StatisticsNotFoundException;
import com.partyhub.PartyHub.repository.StatisticsRepository;
import com.partyhub.PartyHub.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsRepository statisticsRepository;
    @Override
    public Statistics getStatisticsByEventId(UUID eventId) {
        return statisticsRepository.findByEventId(eventId)
                .orElseThrow(() -> new StatisticsNotFoundException("Statistics not found for event ID: " + eventId));
    }

    @Override
    public Statistics save(Statistics statistics) {
        return statisticsRepository.save(statistics);
    }

    }



