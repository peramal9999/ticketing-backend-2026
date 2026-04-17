package com.peramal.ticketingsys.service;

import com.peramal.ticketingsys.dto.request.CreateClientRequest;
import com.peramal.ticketingsys.dto.response.ClientResponse;
import com.peramal.ticketingsys.entity.Client;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    List<ClientResponse> getAll();
    ClientResponse getById(UUID id);
    ClientResponse create(CreateClientRequest request);
    ClientResponse update(UUID id, CreateClientRequest request);
    void delete(UUID id);
    Client findOrThrow(UUID id);
    ClientResponse toResponse(Client client);
}
