package com.partyhub.PartyHub.repository;


import com.partyhub.PartyHub.entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DiscountRepository extends JpaRepository<Discount, UUID> {
}
