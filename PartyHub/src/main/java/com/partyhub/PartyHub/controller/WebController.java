package com.partyhub.PartyHub.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @Value("${server.url}")
    private String serverUrl;
    @GetMapping("/reset-password/{token}")
    public String showResetPasswordForm(Model model) {
        model.addAttribute("serverUrl", serverUrl);
        return "reset-password";

    }
    @GetMapping("/confirm-email/{token}")
    public String showConfirmEmail() {
        return "confirm-email";
    }

}
