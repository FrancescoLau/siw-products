package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AuthenticationService;
import it.uniroma3.siw.service.CommentoService;
import it.uniroma3.siw.service.ProdottoService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping
public class ProdottoController {

    private final ProdottoService prodottoService;

    @Autowired
    public ProdottoController(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
    }

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CommentoService commentoService;

    @GetMapping("/prodotti")
    public String listAll(
        @RequestParam(name = "nome", required = false) String nome,
        @RequestParam(name = "prezzoMin", required = false) Double prezzoMin,
        @RequestParam(name = "prezzoMax", required = false) Double prezzoMax,
        @RequestParam(name = "tipologia", required = false) String tipologia,
        Authentication authentication,
        Model model) {
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                                  && !(authentication instanceof AnonymousAuthenticationToken);
        model.addAttribute("isAuthenticated", isAuthenticated);
        boolean isAdmin = isAuthenticated && authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        
        model.addAttribute("isAdmin", isAdmin);
        
        if (prezzoMin != null && prezzoMax != null && prezzoMin > prezzoMax) {
            model.addAttribute("errorPrezzo", "Prezzo minimo non può essere maggiore del prezzo massimo.");
            // opzionale: azzera i filtri prezzo o skip ricerca
            prezzoMin = null;
            prezzoMax = null;
        }
        List<Prodotto> prodotti = prodottoService.searchProdotti(nome, prezzoMin, prezzoMax, tipologia);
        model.addAttribute("prodotti", prodotti);
        model.addAttribute("nome", nome);
        model.addAttribute("prezzoMin", prezzoMin);
        model.addAttribute("prezzoMax", prezzoMax);
        model.addAttribute("tipologia", tipologia);
        return "prodotti";
    }

    @GetMapping("/prodotti/{id}")
    public String dettaglioProdotto(@PathVariable Long id, Model model) {
        Optional<Prodotto> prodottoOpt = prodottoService.findByIdWithProdottiSimili(id);
        if (prodottoOpt.isEmpty()) {
            System.out.println("Prodotto non trovato per id: " + id);
            return "redirect:/prodotti";
        }
        Prodotto prodotto = prodottoOpt.get();
        model.addAttribute("prodotto", prodotto);

        User currentUser = authenticationService.getCurrentUser();
        System.out.println("Utente attuale: " + (currentUser != null ? currentUser.getNome() : "nessuno"));

        if (currentUser != null) {
            Optional<Commento> commentoOpt = commentoService.findByProdottoAndAutore(prodotto, currentUser);
            model.addAttribute("commentoUtente", commentoOpt.orElse(null));
            model.addAttribute("hasComment", commentoOpt.isPresent());
        } else {
            model.addAttribute("commentoUtente", null);
            model.addAttribute("hasComment", false);
        }

        addAuthenticationStatus(model);
        System.out.println("Modello currentUser dopo addAuthenticationInfo: " + model.getAttribute("currentUser"));

        return "dettagliProdotto";
    }


    // Mostra il form per inserire un nuovo prodotto (solo admin)
    @GetMapping("/adminHome/new")
    public String formNewProdotto(Model model) {
        model.addAttribute("prodotto", new Prodotto());
        List<Prodotto> tuttiProdotti = prodottoService.getAllProdotti();
        model.addAttribute("tuttiProdotti", tuttiProdotti);
        model.addAttribute("prodottiSimiliSelezionati", Collections.emptySet());
        return "newProdotto";  // nome template
    }

    // Cancella un prodotto per id (solo admin)
    @GetMapping("/admin/elimina/{id}")
    public String eliminaProdotto(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        prodottoService.deleteProdotto(id);
        redirectAttrs.addFlashAttribute("successMessage", "Prodotto eliminato con successo");
        return "redirect:/prodotti";
    }


    public void addAuthenticationStatus(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && 
                                 authentication.isAuthenticated() && 
                                 !(authentication instanceof AnonymousAuthenticationToken);
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("isAdmin", false);
        }
    }
    
    @GetMapping("/prodotti/{prodottoId}/commenti")
    public String listCommenti(@PathVariable Long prodottoId, Model model) {
        Optional<Prodotto> prodottoOpt = prodottoService.findById(prodottoId);
        if (prodottoOpt.isEmpty()) return "redirect:/prodotti";
        Prodotto prodotto = prodottoOpt.get();
        List<Commento> commenti = commentoService.findByProdotto(prodotto);
        User currentUser = authenticationService.getCurrentUser();
        Commento commentoUtente = null;
        if (currentUser != null) {
            commentoUtente = commentoService.findByProdottoAndAutore(prodotto, currentUser).orElse(null);
        }
        model.addAttribute("prodotto", prodotto);
        model.addAttribute("commenti", commenti);
        model.addAttribute("isAuthenticated", currentUser != null);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAdmin", currentUser != null && authenticationService.isAdmin());
        model.addAttribute("commentoUtente", commentoUtente);
        return "commentiProdotto"; // Usa il nome esatto del file in templates/
    }


}
