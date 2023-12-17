package com.partyhub.PartyHub.repository;

import com.partyhub.PartyHub.entities.CustomerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerDetailsRepository extends JpaRepository<CustomerDetails, UUID> {
}
