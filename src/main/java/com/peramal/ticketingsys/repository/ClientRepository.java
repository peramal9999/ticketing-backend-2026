package com.peramal.ticketingsys.repository;

import com.peramal.ticketingsys.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
}
