package com.peramal.ticketingsys.service;

import com.peramal.ticketingsys.dto.request.CreateProjectRequest;
import com.peramal.ticketingsys.dto.request.UpdateProjectRequest;
import com.peramal.ticketingsys.dto.response.ProjectResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    List<ProjectResponse> getAll();
    ProjectResponse getById(UUID id);
    List<ProjectResponse> getByClientId(UUID clientId);
    ProjectResponse create(CreateProjectRequest request);
    ProjectResponse update(UUID id, UpdateProjectRequest request);
    void delete(UUID id);
}
