package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.UserDetails;

public interface UserDetailsService {
    void save(UserDetails userDetails);

    UserDetails create(int age, String fullName);
}
