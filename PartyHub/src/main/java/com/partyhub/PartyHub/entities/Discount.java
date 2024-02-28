package com.partyhub.PartyHub.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String code;
    private int discountValue;
    private UUID eventId;
}
