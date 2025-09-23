package it.uniroma3.siw.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@Controller
@RequestMapping("/commenti")
public class CommentoController {

    @Autowired
    private CommentoService commentoService;

    @Autowired
    private ProdottoService prodottoService;

    @Autowired
    private AuthenticationService authenticationService;

    


    @GetMapping("/new/{prodottoId}")
    public String newCommentoForm(@PathVariable Long prodottoId, Model model) {
        User user = authenticationService.getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }
        Optional<Prodotto> prodottoOpt = prodottoService.findById(prodottoId);
        if (prodottoOpt.isEmpty()) {
            return "redirect:/catalogo";
        }
        Prodotto prodotto = prodottoOpt.get();
        // CONTROLLO COMMENTO ESISTENTE
        Optional<Commento> commentoEsistente = commentoService.findByProdottoAndAutore(prodotto, user);
        if (commentoEsistente.isPresent()) {
            // REDIREZIONA ALLA MODIFICA
            return "redirect:/commenti/" + commentoEsistente.get().getId() + "/modifica";
        }
        model.addAttribute("commento", new Commento());
        model.addAttribute("prodotto", prodotto);
        authenticationService.addAuthenticationInfo(model);
        return "newCommento";
    }

    @PostMapping("/new/{prodottoId}")
    public String saveNewCommento(@PathVariable Long prodottoId,
                                  @ModelAttribute Commento commento,
                                  RedirectAttributes redirectAttrs) {
        User user = authenticationService.getCurrentUser();
        if (user == null) {
            redirectAttrs.addFlashAttribute("errorMessage", "Devi essere autenticato per inserire un commento");
            return "redirect:/login";
        }
        Optional<Prodotto> prodottoOpt = prodottoService.findById(prodottoId);
        if (prodottoOpt.isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Prodotto non trovato");
            return "redirect:/prodotti";
        }
        Optional<Commento> commentoEsistente = commentoService.findByProdottoAndAutore(prodottoOpt.get(), user);
        if (commentoEsistente.isPresent()) {
            return "redirect:/commenti/" + commentoEsistente.get().getId() + "/modifica";
        }
        try {
            commento.setAutore(user);
            commento.setProdotto(prodottoOpt.get());
            commentoService.save(commento);
            redirectAttrs.addFlashAttribute("successMessage", "Commento salvato correttamente");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Commento non salvato");
        }
        return "redirect:/prodotti/" + prodottoId;
    }


 // Visualizza form modifica commento
    @GetMapping("/{commentoId}/modifica")
    public String editCommentoForm(@PathVariable Long commentoId, Model model) {
        Optional<Commento> commentoOpt = commentoService.findById(commentoId);
        if (commentoOpt.isEmpty()) return "redirect:/catalogo";
        Commento commento = commentoOpt.get();
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser == null || !commento.getAutore().equals(currentUser)) {
            return "redirect:/catalogo";
        }
        model.addAttribute("commento", commento);
        model.addAttribute("prodotto", commento.getProdotto());
        authenticationService.addAuthenticationInfo(model);
        return "editCommento"; // il template che ti ho mostrato sopra
    }

    // Salva modifiche commento
    @PostMapping("/{commentoId}/modifica")
    public String updateCommento(@PathVariable Long commentoId, @ModelAttribute Commento commentoModificato,
                                RedirectAttributes redirectAttrs) {
        Optional<Commento> commentoOpt = commentoService.findById(commentoId);
        if (commentoOpt.isEmpty()) return "redirect:/catalogo";
        Commento commento = commentoOpt.get();
        User user = authenticationService.getCurrentUser();
        if (!commentoService.canUserModifyComment(user, commento)) {
            return "redirect:/catalogo";
        }
        commento.setTesto(commentoModificato.getTesto());
        commentoService.save(commento);
        redirectAttrs.addFlashAttribute("successMessage", "Commento aggiornato correttamente");
        return "redirect:/prodotti/" + commento.getProdotto().getId();
    }


    @PostMapping("/commento/{commentoId}/delete")
    public String deleteCommento(@PathVariable Long commentoId, RedirectAttributes redirectAttrs) {
        User currentUser = authenticationService.getCurrentUser();
        Optional<Commento> commentoOpt = commentoService.findById(commentoId);
        if (commentoOpt.isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Commento non trovato");
            return "redirect:/prodotti";
        }
        Commento commento = commentoOpt.get();
        if (authenticationService.isAdmin() || commento.getAutore().equals(currentUser)) {
            commentoService.delete(commento);
            redirectAttrs.addFlashAttribute("successMessage", "Commento eliminato correttamente");
            return "redirect:/prodotti/" + commento.getProdotto().getId();
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Permesso negato");
            return "redirect:/prodotti/" + commento.getProdotto().getId();
        }
    }



}
