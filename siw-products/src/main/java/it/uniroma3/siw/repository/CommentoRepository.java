package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.model.User;

public interface CommentoRepository extends CrudRepository<Commento, Long> {


    
    List<Commento> findAll();
	Optional<Commento> findById(Long id);
	
	List<Commento> findByProdotto(Prodotto prodotto);
    List<Commento> findByAutore(User autore);
    Optional<Commento> findByProdottoAndAutore(Prodotto prodotto, User autore);
    boolean existsByProdottoAndAutore(Prodotto prodotto, User autore);
    Commento findById(long id);
}
