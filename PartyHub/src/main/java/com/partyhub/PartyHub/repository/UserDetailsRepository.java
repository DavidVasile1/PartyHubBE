package com.partyhub.PartyHub.repository;

import com.partyhub.PartyHub.entities.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserDetailsRepository extends JpaRepository<UserDetails, UUID> {
}
