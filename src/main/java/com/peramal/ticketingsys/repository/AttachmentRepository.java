package com.peramal.ticketingsys.repository;

import com.peramal.ticketingsys.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByTicketId(UUID ticketId);
}
