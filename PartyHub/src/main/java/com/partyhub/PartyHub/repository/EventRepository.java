package com.partyhub.PartyHub.repository;

import com.partyhub.PartyHub.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    Optional<Event> findTopByDateAfterOrderByDateAsc(LocalDate date);
    List<Event> findByDateAfter(LocalDate date);
    Optional<Event> findTopByCityAndDateAfterOrderByDateAsc(String city, LocalDate date);

}
