package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.exceptions.UserNotFoundException;
import com.partyhub.PartyHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/promo-code")
    public ResponseEntity<String> getPromoCode() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found!"));
            if (user.getPromoCode() == null || user.getPromoCode().isEmpty()) {
                return new ResponseEntity<>("Promo code does not exists", HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(user.getPromoCode(), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/generate-promo-code")
    public ResponseEntity<String> generatePromoCode() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found!"));
            if (user.getPromoCode() == null || user.getPromoCode().isEmpty()) {
                userService.generateAndSetPromoCodeForUser(user.getId());
                return new ResponseEntity<>(user.getPromoCode(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Promo code already exists", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/promo-code")
    public ResponseEntity<String> editPromoCode(@RequestBody String newPromoCode) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found!"));

            if (!isValidPromoCode(newPromoCode)) {
                return new ResponseEntity<>("Invalid promo code format", HttpStatus.BAD_REQUEST);
            }

            if (userService.isPromoCodeInUse(newPromoCode)) {
                return new ResponseEntity<>("Promo code already in use", HttpStatus.BAD_REQUEST);
            }

            user.setPromoCode(newPromoCode);
            userService.save(user);
            return new ResponseEntity<>("Promo code updated successfully", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidPromoCode(String promoCode) {
        if (promoCode == null || promoCode.length() != 9) {
            return false;
        }

        for (int i = 0; i < promoCode.length(); i++) {
            char ch = promoCode.charAt(i);
            if (!Character.isLowerCase(ch) && !Character.isDigit(ch)) {
                return false;
            }
        }

        return true;
    }
}
