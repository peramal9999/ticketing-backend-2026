package com.peramal.ticketingsys.service;

import com.peramal.ticketingsys.dto.response.StatusHistoryResponse;

import java.util.List;
import java.util.UUID;

public interface TicketStatusHistoryService {
    List<StatusHistoryResponse> getHistory(UUID ticketId);
}
