package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.service.CommentoService;
import it.uniroma3.siw.service.ProdottoService;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private ProdottoService prodottoService;
    
    @Autowired
    private CommentoService commentoService;

    // ========== GESTIONE PRODOTTI ==========

    @GetMapping("/adminHome")
    public String adminHome(Model model) {
        List<Prodotto> tuttiProdotti = prodottoService.findAll(); // tutti i prodotti senza limite
        model.addAttribute("prodotti", tuttiProdotti);
        System.out.println("Numero prodotti trovati in adminHome: " + (tuttiProdotti != null ? tuttiProdotti.size() : 0));
        if (tuttiProdotti != null) {
            tuttiProdotti.forEach(p -> System.out.println("Prodotto: " + p.getNome()));
        }

        // Statistiche commenti
        List<Commento> tuttiCommenti = commentoService.findAll();
        model.addAttribute("totalCommenti", tuttiCommenti.size());

        addAuthenticationStatus(model);
        return "adminIndex";
    }


    @PostMapping("/adminHome/save")
    public String saveProdotto(@ModelAttribute Prodotto prodotto,
                               @RequestParam(required = false) List<Long> prodottiSimiliIds,
                               RedirectAttributes redirectAttrs) {
        if (prodottiSimiliIds != null) {
            List<Prodotto> simili = prodottiSimiliIds.stream()
                    .map(pid -> prodottoService.getProdottoById(pid).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());  // <-- usa toList() per mantenere List
            prodotto.setProdottiSimili(simili);
        } else {
            prodotto.setProdottiSimili(Collections.emptyList());  // <-- usa emptyList() per List
        }
        prodottoService.save(prodotto);
        redirectAttrs.addFlashAttribute("successMessage", "Prodotto salvato con successo");
        return "redirect:/adminHome";
    }






    @GetMapping("/adminHome/{id}/edit")
    public String editProdotto(@PathVariable Long id, Model model) {
        Optional<Prodotto> prodottoOpt = prodottoService.findById(id);
        if (prodottoOpt.isEmpty()) {
            return "redirect:/adminHome?error=notfound";
        }
        Prodotto prodotto = prodottoOpt.get();
        model.addAttribute("prodotto", prodotto);

        List<Prodotto> tuttiProdotti = prodottoService.getAllProdotti();
        model.addAttribute("tuttiProdotti", tuttiProdotti);
        Set<Long> similiIds = prodotto.getProdottiSimili().stream()
                .map(Prodotto::getId)
                .collect(Collectors.toSet());
model.addAttribute("prodottiSimiliSelezionati", similiIds);
        return "newProdotto";  // riusa il template per inserimento/modifica
    }


    @PostMapping("/adminHome/{id}")
    public String updateProdotto(@PathVariable("id") Long id,
                                 @ModelAttribute("prodotto") Prodotto prodotto,
                                 @RequestParam(required = false) List<Long> prodottiSimiliIds,
                                 RedirectAttributes redirectAttrs) {
        Optional<Prodotto> esistente = prodottoService.findById(id);
        if (esistente.isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Prodotto non trovato");
            return "redirect:/adminHome";
        }

        prodotto.setId(id);

        if (prodottiSimiliIds != null) {
            List<Prodotto> simili = prodottiSimiliIds.stream()
                .map(pid -> prodottoService.getProdottoById(pid).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());  // Usa toList() per List
            prodotto.setProdottiSimili(simili); 
        } else {
            prodotto.setProdottiSimili(Collections.emptyList());  // Usa emptyList() per List
        }

        prodottoService.save(prodotto);
        redirectAttrs.addFlashAttribute("successMessage", "Prodotto aggiornato con successo");
        return "redirect:/adminHome";
    }



    @PostMapping("/adminHome/{id}/delete")
    public String deleteProdotto(@PathVariable("id") Long id) {
        prodottoService.deleteProdotto(id);
        return "redirect:/adminHome";
    }

    // ========== GESTIONE COMMENTI (ADMIN) ==========

    @GetMapping("/adminHome/commenti")
    public String adminCommenti(Model model) {
        if (!isCurrentUserAdmin()) {
            return "redirect:/";
        }
        
        model.addAttribute("commenti", commentoService.findAll());
        addAuthenticationStatus(model);
        return "adminCommenti";
    }

    // Metodo helper per controllo ruolo admin
    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !(authentication instanceof AnonymousAuthenticationToken) &&
               authentication.getAuthorities().stream()
                   .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
    }

    // Metodo helper per info autenticazione da passare al modello
    private void addAuthenticationStatus(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("isAdmin", isAuthenticated && isCurrentUserAdmin());
    }
}
