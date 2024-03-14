package com.partyhub.PartyHub.repository;

import com.partyhub.PartyHub.entities.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StatisticsRepository extends JpaRepository<Statistics, UUID> {
    Optional<Statistics> findByEventId(UUID eventId);
}