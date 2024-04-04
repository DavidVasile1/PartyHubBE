package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.Statistics;

import java.util.UUID;
public interface StatisticsService {
     Statistics getStatisticsByEventId(UUID eventId);
    Statistics save(Statistics statistics);

}
