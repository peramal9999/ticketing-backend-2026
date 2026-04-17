package com.peramal.ticketingsys.repository;

import com.peramal.ticketingsys.entity.TicketStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketStatusHistoryRepository extends JpaRepository<TicketStatusHistory, UUID> {
    List<TicketStatusHistory> findByTicketIdOrderByCreatedAtAsc(UUID ticketId);
}
