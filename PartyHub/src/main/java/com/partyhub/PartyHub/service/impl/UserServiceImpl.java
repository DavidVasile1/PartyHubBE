package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.repository.UserRepository;
import com.partyhub.PartyHub.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    @Override
    public void save(User user) {
        userRepository.save(user);
    }
    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }
}

