package com.peramal.ticketingsys.service.impl;

import com.peramal.ticketingsys.dto.request.CreateProjectRequest;
import com.peramal.ticketingsys.dto.request.UpdateProjectRequest;
import com.peramal.ticketingsys.dto.response.ProjectResponse;
import com.peramal.ticketingsys.entity.Client;
import com.peramal.ticketingsys.entity.Project;
import com.peramal.ticketingsys.exception.ResourceNotFoundException;
import com.peramal.ticketingsys.repository.ProjectRepository;
import com.peramal.ticketingsys.service.ClientService;
import com.peramal.ticketingsys.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ClientService clientService;

    @Override
    public List<ProjectResponse> getAll() {
        return projectRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ProjectResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<ProjectResponse> getByClientId(UUID clientId) {
        return projectRepository.findByClientId(clientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ProjectResponse create(CreateProjectRequest req) {
        Client client = clientService.findOrThrow(req.getClientId());
        if (projectRepository.existsByShortCodeIgnoreCase(req.getShortCode())) {
            log.warn("Project creation failed - short code already in use: {}", req.getShortCode().toUpperCase());
            throw new IllegalArgumentException("Short code already in use: " + req.getShortCode().toUpperCase());
        }
        Project project = Project.builder()
                .client(client)
                .name(req.getName())
                .shortCode(req.getShortCode().toUpperCase())
                .description(req.getDescription())
                .isActive(req.getIsActive() != null ? req.getIsActive() : false)
                .build();
        ProjectResponse response = toResponse(projectRepository.save(project));
        log.info("Project created: id={}, name={}, shortCode={}, clientId={}",
                response.getId(), response.getName(), response.getShortCode(), req.getClientId());
        return response;
    }

    @Override
    public ProjectResponse update(UUID id, UpdateProjectRequest req) {
        Project project = findOrThrow(id);
        if (req.getName() != null) project.setName(req.getName());
        if (req.getDescription() != null) project.setDescription(req.getDescription());
        if (req.getIsActive() != null) project.setIsActive(req.getIsActive());
        if (req.getClientId() != null) {
            Client client = clientService.findOrThrow(req.getClientId());
            project.setClient(client);
        }
        ProjectResponse response = toResponse(projectRepository.save(project));
        log.info("Project updated: id={}", id);
        return response;
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        projectRepository.deleteById(id);
        log.info("Project deleted: id={}", id);
    }

    private Project findOrThrow(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
    }

    private ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .clientId(project.getClient().getId())
                .clientName(project.getClient().getName())
                .name(project.getName())
                .shortCode(project.getShortCode())
                .description(project.getDescription())
                .isActive(project.getIsActive())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
