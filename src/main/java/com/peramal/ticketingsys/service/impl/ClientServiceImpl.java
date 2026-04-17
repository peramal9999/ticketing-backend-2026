package com.peramal.ticketingsys.service.impl;

import com.peramal.ticketingsys.dto.request.CreateClientRequest;
import com.peramal.ticketingsys.dto.response.ClientResponse;
import com.peramal.ticketingsys.entity.Client;
import com.peramal.ticketingsys.exception.ResourceNotFoundException;
import com.peramal.ticketingsys.repository.ClientRepository;
import com.peramal.ticketingsys.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public List<ClientResponse> getAll() {
        return clientRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ClientResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public ClientResponse create(CreateClientRequest req) {
        Client client = Client.builder()
                .name(req.getName())
                .contactEmail(req.getContactEmail())
                .build();
        ClientResponse response = toResponse(clientRepository.save(client));
        log.info("Client created: id={}, name={}", response.getId(), response.getName());
        return response;
    }

    @Override
    public ClientResponse update(UUID id, CreateClientRequest req) {
        Client client = findOrThrow(id);
        if (req.getName() != null) client.setName(req.getName());
        if (req.getContactEmail() != null) client.setContactEmail(req.getContactEmail());
        ClientResponse response = toResponse(clientRepository.save(client));
        log.info("Client updated: id={}", id);
        return response;
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        clientRepository.deleteById(id);
        log.info("Client deleted: id={}", id);
    }

    @Override
    public Client findOrThrow(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
    }

    @Override
    public ClientResponse toResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .contactEmail(client.getContactEmail())
                .createdAt(client.getCreatedAt())
                .build();
    }
}
