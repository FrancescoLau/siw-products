package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Credentials;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {

    // Permette di cercare credenziali per username (utile per autenticazione)
    Credentials findByUsername(String username);
}
