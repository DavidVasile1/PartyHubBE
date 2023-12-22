package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    void save(User user);
}
