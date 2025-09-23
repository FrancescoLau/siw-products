package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.service.CredentialsService;

@Controller
public class CredentialsController {

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping("/credentials/change-password")
    public String showChangePasswordForm() {
        return "changePassword"; // restituisce changePassword.html
    }

    @PostMapping("/credentials/change-password")
    public String changePassword(@RequestParam String username, @RequestParam String newPassword, Model model) {
        boolean success = credentialsService.changePassword(username, newPassword);

        if (success) {
            model.addAttribute("successMessage", "Password cambiata con successo");
        } else {
            model.addAttribute("errorMessage", "Errore nel cambio password");
        }
        return "changePassword";
    }
}
