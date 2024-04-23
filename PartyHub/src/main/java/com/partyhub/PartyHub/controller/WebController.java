package com.partyhub.PartyHub.controller;


import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.exceptions.UserAlreadyVerifiedException;
import com.partyhub.PartyHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WebController {

    @Value("${server.url}")
    private String serverUrl;
    private final UserService userService;

    @GetMapping("/reset-password/{token}")
    public String showResetPasswordForm(Model model) {
        model.addAttribute("serverUrl", serverUrl);
        return "reset-password";

    }

    @GetMapping("/confirm-email/{token}")
    public String showConfirmEmail(@PathVariable UUID token) {
        User user = this.userService.findByVerificationToken(token);

        if (!user.isVerified()) {
            user.setVerified(true);
            this.userService.save(user);
        } else {
            throw new UserAlreadyVerifiedException("User already verified!");
        }
        return "confirm-email";
    }

    @GetMapping("/terms-and-conditions")
    public String showTermsAndConditions() {
        return "termeni-si-conditii";
    }

    @GetMapping("/privacy-policy")
    public String showPrivacyPolicies() {
        return "politica-confidentialitate";
    }
    @GetMapping("/delete-account")
    public String showDeleteAccount() {
        return "delete-account";
    }

}
