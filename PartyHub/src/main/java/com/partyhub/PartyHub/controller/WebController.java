package com.partyhub.PartyHub.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("/reset-password/{token}")
    public String showResetPasswordForm() {
        // Aici poți adăuga token-ul la model, dacă pagina ta HTML sau template-ul are nevoie de el
//        model.addAttribute("token", token);

        // Numele fișierului HTML (fără .html) din directorul `src/main/resources/templates`
        // Sau doar returnează conținut static dacă pagina este în `src/main/resources/static`
        return "reset-password";
    }
}
