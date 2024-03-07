package com.partyhub.PartyHub.repository;


import com.partyhub.PartyHub.entities.Discount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface DiscountRepository extends JpaRepository<Discount, UUID> {
    Optional<Discount> findByCode(String code);
    @Transactional
    @Modifying
    @Query("DELETE FROM Discount d WHERE d.code = ?1")
    void deleteByCode(String code);
}
