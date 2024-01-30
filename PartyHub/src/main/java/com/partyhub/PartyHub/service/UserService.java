package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(UUID verificationToken);
    Boolean existsByEmail(String email);
    void save(User user);
    void delete(User user);
    public void generateAndSetPromoCodeForUser(UUID userId);
    public boolean isPromoCodeInUse(String promoCode);
}
