package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.entities.UserDetails;
import com.partyhub.PartyHub.repository.UserDetailsRepository;
import com.partyhub.PartyHub.service.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserDetailsRepository userDetailsRepository;

    @Override
    public void save(UserDetails userDetails) {
        userDetailsRepository.save(userDetails);
    }

    @Override
    public UserDetails create(int age, String fullName) {
        UserDetails userDetails = new UserDetails();
        userDetails.setAge(age);
        userDetails.setFullName(fullName);
        userDetails.setDiscountForNextTicket(0);
        save(userDetails);
        return userDetails;
    }

    @Override
    public Optional<UserDetails> findById(UUID id) {
        return userDetailsRepository.findById(id);
    }

    @Override
    public void delete(UserDetails userDetails) {
        userDetailsRepository.delete(userDetails);
    }
}
