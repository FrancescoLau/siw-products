package it.uniroma3.siw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CommentoRepository;

@Service
public class CommentoService {

    @Autowired
    private CommentoRepository commentoRepository;

    public Optional<Commento> findById(Long id) {
        return commentoRepository.findById(id);
    }

    public boolean canUserModifyComment(User user, Commento commento) {
        if (user == null || commento == null) return false;
        return user.equals(commento.getAutore());
    }
    
    public List<Commento> findAll(){
    	return commentoRepository.findAll();
    }
	
    public Commento save(Commento commento) {
        return commentoRepository.save(commento);
    }

    public Commento findById(long id) {
        return commentoRepository.findById(id);
    }

    public List<Commento> findByProdotto(Prodotto prodotto) {
        return commentoRepository.findByProdotto(prodotto);
    }

    public List<Commento> findByAutore(User autore) {
        return commentoRepository.findByAutore(autore);
    }

    public Optional<Commento> findByProdottoAndAutore(Prodotto prodotto, User autore) {
        return commentoRepository.findByProdottoAndAutore(prodotto, autore);
    }

    public boolean existsByProdottoAndAutore(Prodotto prodotto, User autore) {
        return commentoRepository.existsByProdottoAndAutore(prodotto, autore);
    }

    public void delete(Commento commento) {
        commentoRepository.delete(commento);
    }

    public void deleteById(long id) {
        commentoRepository.deleteById(id);
    }

    public boolean canUserModify(Commento commento, User user) {
        return user != null && commento.getAutore().equals(user);
    }
}

