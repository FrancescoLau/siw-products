package it.uniroma3.siw.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Commento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String testo;

    @ManyToOne(optional = false)
    private User autore;

    @ManyToOne(optional = false)
    private Prodotto prodotto; // Assumendo che ci sia l'entità Prodotto per il catalogo

    @Column(nullable = false)
    private LocalDateTime dataCreazione;

    // Costruttori, getter e setter

    public Commento() {
        this.dataCreazione = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public User getAutore() { return autore; }
    public void setAutore(User autore) { this.autore = autore; }

    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }
}
