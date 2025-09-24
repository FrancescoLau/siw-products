package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

    // Cerca un utente per email (utile per registrazione e login)
    User findByEmail(String email);
}
