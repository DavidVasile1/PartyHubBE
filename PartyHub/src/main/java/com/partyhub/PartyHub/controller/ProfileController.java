package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.ProfileDto;
import com.partyhub.PartyHub.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;


    @GetMapping("/{email}")
    public ProfileDto getProfile(@PathVariable String email) {
        ProfileDto profile = profileService.getProfile(email);

        if (profile != null) {
            return profile;
        } else {
            return new ProfileDto();
        }
    }
}
