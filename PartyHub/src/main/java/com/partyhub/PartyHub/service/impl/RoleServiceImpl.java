package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.entities.Role;
import com.partyhub.PartyHub.exceptions.RoleNotFoundException;
import com.partyhub.PartyHub.repository.RoleRepository;
import com.partyhub.PartyHub.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;


@Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + name));
    }

    @Override
    public Boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}

