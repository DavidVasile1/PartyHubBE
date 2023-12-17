package com.partyhub.PartyHub.repository;

import com.partyhub.PartyHub.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
}
