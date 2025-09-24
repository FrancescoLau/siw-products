package it.uniroma3.siw.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository; // Assicurati di avere questo import!
import it.uniroma3.siw.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CredentialsRepository credentialsRepository; // repository iniettato correttamente

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttrs) {
        if(result.hasErrors()) {
            return "register";
        }
        userService.save(user);
        redirectAttrs.addFlashAttribute("successMessage", "Registrazione avvenuta con successo");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // restituisce login.html
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register"; // restituisce register.html
    }

    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        String username = authentication.getName();

        Credentials credentials = credentialsRepository.findByUsername(username);
        if (credentials == null) {
            model.addAttribute("error", "Credenziali non trovate");
            return "profilo";
        }

        User user = credentials.getUser();

        user.setCredentials(credentials);

        model.addAttribute("user", user);

        return "profilo";
    }

    
    

}
