package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Prodotto;

public interface ProdottoRepository extends CrudRepository<Prodotto, Long>{
	
	 // Metodo per cercare prodotti per nome
    List<Prodotto> findByNomeContainingIgnoreCase(String nome);

    // Metodo per cercare prodotti per tipologia 
    List<Prodotto> findByTipologia(String tipologia);
    
    //Metodo per restituire tutti i prodotti
    List<Prodotto> findAll();
    
    Optional findById(Long prodottoId);
    
    void deleteById(Long prodottoId);
    Optional<Prodotto> findByNome(String nome);
    
    @Query("SELECT p FROM Prodotto p LEFT JOIN FETCH p.prodottiSimili WHERE p.id = :id")
    Optional<Prodotto> findByIdWithProdottiSimili(@Param("id") Long id);


    @Query("SELECT p FROM Prodotto p " +
    	       "WHERE (:nome IS NULL OR p.nome LIKE %:nome%) " +
    	       "AND (:tipologia IS NULL OR :tipologia = '' OR p.tipologia = :tipologia) " +
    	       "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) " +
    	       "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax)")
    	List<Prodotto> searchProdottiByParams(@Param("nome") String nome,
    	                                     @Param("tipologia") String tipologia,
    	                                     @Param("prezzoMin") Double prezzoMin,
    	                                     @Param("prezzoMax") Double prezzoMax);



}
