package it.uniroma3.siw.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.service.AuthenticationService;
import it.uniroma3.siw.service.ProdottoService;

@Controller
public class HomeController {

    @Autowired
    private ProdottoService prodottoService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping({"/", "/index"})
    public String homePage(Model model, Authentication authentication) {
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && 
            !(authentication instanceof AnonymousAuthenticationToken);
        model.addAttribute("isAuthenticated", isAuthenticated);

        // Prendi tutti i prodotti
        List<Prodotto> tuttiProdotti = prodottoService.getAllProdotti();
        
        // Limita ai primi 3 prodotti per la homepage
        List<Prodotto> prodottiTop3 = tuttiProdotti.stream()
                                        .limit(3)
                                        .collect(Collectors.toList());
        
        // Metti nel modello solo i primi 3 prodotti
        model.addAttribute("prodotti", prodottiTop3);

        return "index";
    }
    
    
    @GetMapping("/product/details")
    public String dettagliProdotto(@RequestParam Long id, Model model) {
        Optional<Prodotto> prodotto = prodottoService.getProdottoById(id);
        if (prodotto.isPresent()) {
            model.addAttribute("prodotto", prodotto.get());
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                                      && !(authentication instanceof AnonymousAuthenticationToken);
            model.addAttribute("isAuthenticated", isAuthenticated);

            if (isAuthenticated) {
                boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
                model.addAttribute("isAdmin", isAdmin);
            } else {
                model.addAttribute("isAdmin", false);
            }
            
            return "dettagliProdotto";
        } else {
            return "redirect:/catalogo";
        }
    }

    
    

}
