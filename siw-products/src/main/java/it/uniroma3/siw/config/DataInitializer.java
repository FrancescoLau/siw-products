package it.uniroma3.siw.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ProdottoService;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private ProdottoService prodottoService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // Crea admin se non esiste
        if (credentialsService.findByUsername("admin") == null) {
            Credentials adminCredentials = new Credentials();
            adminCredentials.setUsername("admin");
            adminCredentials.setPassword("admin"); 
            adminCredentials.setRole(Credentials.ADMIN_ROLE);

            User adminUser = new User();
            adminUser.setNome("Admin");
            adminUser.setCognome("Sistema");
            adminUser.setEmail("admin@product.com");
            adminUser.setRole(User.Role.ADMIN);

            adminCredentials.setUser(adminUser);
            credentialsService.save(adminCredentials);

            System.out.println("✅ Utente amministratore creato con successo!");
        } else {
            System.out.println("ℹ️ Utente amministratore già esistente.");
        }
        
        // Crea utente di test per debugging
        if (credentialsService.findByUsername("testuser") == null) {
            Credentials testCredentials = new Credentials();
            testCredentials.setUsername("testuser");
            testCredentials.setPassword("test123");
            testCredentials.setRole(Credentials.DEFAULT_ROLE);

            User testUser = new User();
            testUser.setNome("Test");
            testUser.setCognome("User");
            testUser.setEmail("test@product.com");
            testUser.setRole(User.Role.DEFAULT);

            testCredentials.setUser(testUser);
            credentialsService.save(testCredentials);

            System.out.println("✅ Utente di test creato!");
        }

        // Garantisce la presenza/aggiornamento dei prodotti iniziali
        inserisciODaModificaProdotto("Libro Java", 29.99, "Libro", "Una guida completa per imparare Java.");
        inserisciODaModificaProdotto("Tazza da Caffè", 9.99, "Accessorio", "Tazza elegante per la tua pausa caffè.");
        inserisciODaModificaProdotto("Zaino da Trekking", 79.99, "Outdoor", "Zaino resistente e capiente per le tue avventure.");

        System.out.println("✅ Prodotti iniziali presenti o aggiornati con successo!");
    }

    private void inserisciODaModificaProdotto(String nome, double prezzo, String tipologia, String descrizione) {
        Optional<Prodotto> pOpt = prodottoService.findByNome(nome);
        Prodotto prodotto;
        if (pOpt.isPresent()) {
            prodotto = pOpt.get();
            prodotto.setPrezzo(prezzo);
            prodotto.setTipologia(tipologia);
            prodotto.setDescrizione(descrizione);
            System.out.println("ℹ️ Prodotto aggiornato: " + nome);
        } else {
            prodotto = new Prodotto();
            prodotto.setNome(nome);
            prodotto.setPrezzo(prezzo);
            prodotto.setTipologia(tipologia);
            prodotto.setDescrizione(descrizione);
            System.out.println("✅ Prodotto creato: " + nome);
        }
        prodottoService.save(prodotto);
    }
}
