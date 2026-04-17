package com.peramal.ticketingsys.service.impl;

import com.peramal.ticketingsys.dto.response.StatusHistoryResponse;
import com.peramal.ticketingsys.exception.ResourceNotFoundException;
import com.peramal.ticketingsys.repository.TicketRepository;
import com.peramal.ticketingsys.repository.TicketStatusHistoryRepository;
import com.peramal.ticketingsys.service.TicketStatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketStatusHistoryServiceImpl implements TicketStatusHistoryService {

    private final TicketStatusHistoryRepository historyRepository;
    private final TicketRepository ticketRepository;

    @Override
    public List<StatusHistoryResponse> getHistory(UUID ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket", "id", ticketId);
        }
        return historyRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(h -> StatusHistoryResponse.builder()
                        .id(h.getId())
                        .ticketId(h.getTicket().getId())
                        .oldStatus(h.getOldStatus())
                        .newStatus(h.getNewStatus())
                        .changedById(h.getChangedBy() != null ? h.getChangedBy().getId() : null)
                        .changedByName(h.getChangedBy() != null ?
                                h.getChangedBy().getFirstName() + " " + h.getChangedBy().getLastName() : null)
                        .createdAt(h.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
