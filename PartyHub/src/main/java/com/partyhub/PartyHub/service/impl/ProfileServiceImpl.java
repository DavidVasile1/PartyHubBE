package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.dto.ProfileDto;
import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.entities.UserDetails;
import com.partyhub.PartyHub.service.ProfileService;
import com.partyhub.PartyHub.service.UserService;
import com.partyhub.PartyHub.service.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserService userService;
    private final UserDetailsService userDetailsService;

    public ProfileServiceImpl(UserService userService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public ProfileDto getProfile(String email) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        Optional<UserDetails> detailsOptional = userDetailsService.findById(user.getId());

        if (detailsOptional.isEmpty()) {
            return null;
        }

        UserDetails details = detailsOptional.get();

        ProfileDto profile = new ProfileDto();
        profile.setUserId(user.getId());
        profile.setEmail(user.getEmail());
        profile.setFullName(details.getFullName());
        profile.setAge(details.getAge());

        return profile;
    }
}
