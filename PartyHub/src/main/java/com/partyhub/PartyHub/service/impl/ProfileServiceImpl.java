package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.dto.ProfileDto;
import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.entities.UserDetails;
import com.partyhub.PartyHub.service.ProfileService;
import com.partyhub.PartyHub.service.UserService;
import com.partyhub.PartyHub.service.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserService userService;
    private final UserDetailsService userDetailsService;


    @Override
    public ProfileDto getProfile(String email) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found by email.");
        }

        User user = userOptional.get();
        UserDetails details =user.getUserDetails();

        ProfileDto profile = new ProfileDto();
        profile.setUserId(user.getId());
        profile.setEmail(user.getEmail());
        profile.setFullName(details.getFullName());
        profile.setAge(details.getAge());

        return profile;
    }
}
