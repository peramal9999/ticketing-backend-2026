package com.peramal.ticketingsys.repository;

import com.peramal.ticketingsys.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByClientId(UUID clientId);
    boolean existsByShortCodeIgnoreCase(String shortCode);
}
