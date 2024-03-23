package com.partyhub.PartyHub.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String fullName;
    private int age;
    @OneToMany(mappedBy = "userDetails")
    private List<DiscountForNextTicket> discountForNextTickets;
}
