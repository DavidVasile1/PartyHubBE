package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(String name);
    Boolean existsByName(String name);
}
