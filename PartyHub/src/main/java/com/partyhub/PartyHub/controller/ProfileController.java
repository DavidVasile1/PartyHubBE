package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.ProfileDto;
import com.partyhub.PartyHub.exceptions.UserNotFoundException;
import com.partyhub.PartyHub.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/profiles")
public class ProfileController {

    private final ProfileService profileService;


    @GetMapping("{email}")
    public ResponseEntity<ProfileDto>  getProfile(@PathVariable String email) {
        try {
            ProfileDto profile = profileService.getProfile(email);
            return ResponseEntity.ok(profile);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("{email}")
    public ResponseEntity<String> updateProfile(@PathVariable String email, @RequestBody ProfileDto updatedProfile) {
        try {
            profileService.updateProfileDetails(email, updatedProfile);
            return ResponseEntity.ok("Profile updated successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update profile");
        }
    }

    @DeleteMapping("{email}")
    public ResponseEntity<String> deleteProfile(@PathVariable String email) {
        try {
            profileService.deleteProfile(email);
            return ResponseEntity.ok("Profile deleted successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete profile");
        }
    }
}
