package it.uniroma3.siw.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "prodotti")
public class Prodotto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable=false)
    private String nome;
    
    @Column(nullable=false)
    private Double prezzo;

    @Column(length = 1000)
    private String descrizione;

    private String tipologia;

    // Relazione molti a molti con prodotti simili
 
    @ManyToMany
    @JoinTable(
        name = "prodotti_simili",
        joinColumns = @JoinColumn(name = "prodotto_id"),
        inverseJoinColumns = @JoinColumn(name = "prodotto_simile_id")
    )
    private List<Prodotto> prodottiSimili = new ArrayList<>();


    
    @OneToMany(mappedBy="prodotto", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Commento> commenti;



    public List<Commento> getCommenti() {
		return commenti;
	}

	public void setCommenti(List<Commento> commenti) {
		this.commenti = commenti;
	}

	/*COSTRUTTORI*/
    public Prodotto() {
    }

    public Prodotto(String nome, Double prezzo, String descrizione, String tipologia) {
        this.nome = nome;
        this.prezzo = prezzo;
        this.descrizione = descrizione;
        this.tipologia = tipologia;
    }

    /*GETTER SETTER*/
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(Double prezzo) {
        this.prezzo = prezzo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    
    
    public List<Prodotto> getProdottiSimili() {
		return prodottiSimili;
	}

	public void setProdottiSimili(List<Prodotto> prodottiSimili) {
		this.prodottiSimili = prodottiSimili;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prodotto prodotto = (Prodotto) o;
        return id != null && id.equals(prodotto.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    
}
