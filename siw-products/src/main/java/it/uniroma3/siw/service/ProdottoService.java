package it.uniroma3.siw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.ProdottoRepository;
import jakarta.transaction.Transactional;

@Service
public class ProdottoService {

    @Autowired
    private ProdottoRepository prodottoRepository;

    // Restituisce tutti i prodotti
    public List<Prodotto> getAllProdotti() {
        return prodottoRepository.findAll();
    }

    // Restituisce un prodotto per id, opzionale perché potrebbe non esistere
    public Optional<Prodotto> getProdottoById(Long id) {
        return prodottoRepository.findById(id);
    }

    // Cerca prodotti per nome 
    public List<Prodotto> searchProdottiByNome(String nome) {
        return prodottoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Cerca prodotti per tipologia esatta
    public List<Prodotto> getProdottiByTipologia(String tipologia) {
        return prodottoRepository.findByTipologia(tipologia);
    }

    // Salva o aggiorna un prodotto
    public Prodotto save(Prodotto prodotto) {
        return prodottoRepository.save(prodotto);
    }

    // Elimina un prodotto per id
    @Transactional
    public void deleteProdotto(Long id) {
        Optional<Prodotto> prodottoOpt = prodottoRepository.findById(id);
        if (prodottoOpt.isPresent()) {
            Prodotto prodotto = prodottoOpt.get();

            List<Prodotto> tuttiProdotti = prodottoRepository.findAll();
            for (Prodotto p : tuttiProdotti) {
                if (p.getProdottiSimili().contains(prodotto)) {
                    p.getProdottiSimili().remove(prodotto);
                    prodottoRepository.save(p);
                }
            }

            prodottoRepository.delete(prodotto);
        }
    }


	public List<Prodotto> findAll() {
		return prodottoRepository.findAll();
	}

	public Optional<Prodotto> findById(Long prodottoId) {
		return prodottoRepository.findById(prodottoId);
	}

	
	
	// Conta il numero totale di prodotti
    public long count() {
        return prodottoRepository.count();
    }
    
    public Optional<Prodotto> findByNome(String nome) {
        return prodottoRepository.findByNome(nome);
    }
    
    public List<Prodotto> searchProdotti(String nome, Double prezzoMin, Double prezzoMax, String tipologia) {
        return prodottoRepository.searchProdottiByParams(nome, tipologia, prezzoMin, prezzoMax);
    }


	public Optional<Prodotto> findByIdWithProdottiSimili(Long id) {
		return prodottoRepository.findByIdWithProdottiSimili(id);
	}


}