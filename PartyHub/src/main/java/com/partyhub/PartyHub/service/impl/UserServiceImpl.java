package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.exceptions.UserNotFoundException;
import com.partyhub.PartyHub.repository.UserRepository;
import com.partyhub.PartyHub.service.UserService;
import com.partyhub.PartyHub.util.PromoCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
    @Override
    public User findByVerificationToken(UUID verificationToken) {
        return userRepository.findByVerificationToken(verificationToken)
                .orElseThrow(() -> new UserNotFoundException("User not found with verification token: " + verificationToken));
    }
  
    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
  
    @Override
    public void save(User user) {
        if (user.getPromoCode() == null || user.getPromoCode().isEmpty()) {
            String promoCode;
            do {
                promoCode = PromoCodeGenerator.generatePromoCode(user.getUserDetails().getFullName());
            } while (userRepository.existsByPromoCode(promoCode));
            user.setPromoCode(promoCode);
        }
        userRepository.save(user);
    }
  
    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public void generateAndSetPromoCodeForUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getPromoCode() == null || user.getPromoCode().isEmpty()) {
            String promoCode;
            do {
                promoCode = PromoCodeGenerator.generatePromoCode(user.getUserDetails().getFullName());
            } while (userRepository.existsByPromoCode(promoCode));

            user.setPromoCode(promoCode);
            userRepository.save(user);
        }
    }
  
    @Override
    public boolean isPromoCodeInUse(String promoCode) {
        return userRepository.existsByPromoCode(promoCode);
    }

    @Override
    public boolean doesPromoCodeExist(String promoCode) {
        return userRepository.findByPromoCode(promoCode).isPresent();
    }
    @Override
    public User findByPromoCode(String promoCode) {
        return userRepository.findByPromoCode(promoCode)
                .orElseThrow(() -> new UserNotFoundException("User not found with promo code: " + promoCode));
    }


}

