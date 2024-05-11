package com.partyhub.PartyHub.repository;

import com.partyhub.PartyHub.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByDateAfter(LocalDate date);

   Optional<Event> findTopByCityAndDateGreaterThanEqualOrderByDateAsc(String city,LocalDate today);

    Optional<Event> findTopByDateGreaterThanEqualOrderByDateAsc(LocalDate today);
}
