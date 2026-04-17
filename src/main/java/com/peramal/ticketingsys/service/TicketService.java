package com.peramal.ticketingsys.service;

import com.peramal.ticketingsys.dto.request.AssignTicketRequest;
import com.peramal.ticketingsys.dto.request.ChangeStatusRequest;
import com.peramal.ticketingsys.dto.request.CreateTicketRequest;
import com.peramal.ticketingsys.dto.request.UpdateTicketRequest;
import com.peramal.ticketingsys.dto.response.TicketResponse;
import com.peramal.ticketingsys.entity.enums.Priority;
import com.peramal.ticketingsys.entity.enums.TicketStatus;

import java.util.List;
import java.util.UUID;

public interface TicketService {
    TicketResponse create(CreateTicketRequest request);
    List<TicketResponse> getAll(TicketStatus status, Priority priority, UUID clientId, UUID projectId);
    TicketResponse getById(UUID id);
    TicketResponse update(UUID id, UpdateTicketRequest request);
    void delete(UUID id);
    TicketResponse assign(UUID id, AssignTicketRequest request);
    TicketResponse changeStatus(UUID id, ChangeStatusRequest request);
}
