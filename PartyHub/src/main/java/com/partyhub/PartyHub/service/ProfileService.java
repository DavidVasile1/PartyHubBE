package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.dto.ProfileDto;
import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.entities.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface ProfileService {
    ProfileDto getProfile(String email);
}