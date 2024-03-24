package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface UserDetailsService {
    void save(UserDetails userDetails);

    UserDetails create(int age, String fullName);

    UserDetails findById(UUID id);
    void delete(UserDetails userDetails);
}
