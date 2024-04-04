package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.User;

import java.util.UUID;

public interface UserService {
    User findByEmail(String email);
    User findByVerificationToken(UUID verificationToken);
    Boolean existsByEmail(String email);
    void save(User user);
    void delete(User user);
     void generateAndSetPromoCodeForUser(UUID userId);
     boolean isPromoCodeInUse(String promoCode);
     boolean doesPromoCodeExist(String promoCode);
     User findByPromoCode(String promoCode);




}
