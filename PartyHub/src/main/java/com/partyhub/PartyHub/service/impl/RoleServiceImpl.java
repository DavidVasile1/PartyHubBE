package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.entities.Role;
import com.partyhub.PartyHub.repository.RoleRepository;
import com.partyhub.PartyHub.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;


    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}

