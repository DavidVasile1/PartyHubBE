package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.Role;


public interface RoleService {
    Role findByName(String name);
    Boolean existsByName(String name);
}
