package com.peramal.ticketingsys.service.impl;

import com.peramal.ticketingsys.dto.request.AssignTicketRequest;
import com.peramal.ticketingsys.dto.request.ChangeStatusRequest;
import com.peramal.ticketingsys.dto.request.CreateTicketRequest;
import com.peramal.ticketingsys.dto.request.UpdateTicketRequest;
import com.peramal.ticketingsys.dto.response.TicketResponse;
import com.peramal.ticketingsys.entity.*;
import com.peramal.ticketingsys.entity.enums.Priority;
import com.peramal.ticketingsys.entity.enums.TicketStatus;
import com.peramal.ticketingsys.exception.ResourceNotFoundException;
import com.peramal.ticketingsys.repository.*;
import com.peramal.ticketingsys.service.TicketService;
import com.peramal.ticketingsys.util.SecurityUtils;
import com.peramal.ticketingsys.util.TicketNumberGenerator;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;
    private final TicketStatusHistoryRepository statusHistoryRepository;
    private final TicketNumberGenerator ticketNumberGenerator;

    @Override
    @Transactional
    public TicketResponse create(CreateTicketRequest req) {
        User currentUser = SecurityUtils.getCurrentUser();

        // Resolve project first so we can use its shortCode as the ticket number prefix
        Project project = null;
        if (req.getProjectId() != null) {
            project = projectRepository.findById(req.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", req.getProjectId()));
        }

        String prefix = (project != null) ? project.getShortCode() : null;

        Ticket ticket = Ticket.builder()
                .ticketNumber(ticketNumberGenerator.generate(prefix))
                .title(req.getTitle())
                .description(req.getDescription())
                .priority(req.getPriority() != null ? req.getPriority() : Priority.MEDIUM)
                .status(TicketStatus.OPEN)
                .createdBy(currentUser)
                .build();

        if (project != null) {
            ticket.setClient(project.getClient());
            ticket.setProject(project);
        }
        if (req.getCategoryId() != null) {
            ticket.setCategory(categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", req.getCategoryId())));
        }
        if (req.getAssignedTo() != null) {
            ticket.setAssignedTo(userRepository.findById(req.getAssignedTo())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", req.getAssignedTo())));
        }

        ticket = ticketRepository.save(ticket);

        log.info("Ticket created: ticketNumber={}, createdBy={}", ticket.getTicketNumber(), currentUser.getEmail());
        statusHistoryRepository.save(TicketStatusHistory.builder()
                .ticket(ticket)
                .oldStatus(null)
                .newStatus(TicketStatus.OPEN.name())
                .changedBy(currentUser)
                .build());

        return toResponse(ticket);
    }

    @Override
    public List<TicketResponse> getAll(TicketStatus status, Priority priority, UUID clientId, UUID projectId) {
        Specification<Ticket> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (priority != null) predicates.add(cb.equal(root.get("priority"), priority));
            if (clientId != null) predicates.add(cb.equal(root.get("client").get("id"), clientId));
            if (projectId != null) predicates.add(cb.equal(root.get("project").get("id"), projectId));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return ticketRepository.findAll(spec).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public TicketResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public TicketResponse update(UUID id, UpdateTicketRequest req) {
        Ticket ticket = findOrThrow(id);
        if (req.getTitle() != null) ticket.setTitle(req.getTitle());
        if (req.getDescription() != null) ticket.setDescription(req.getDescription());
        if (req.getPriority() != null) ticket.setPriority(req.getPriority());
        if (req.getProjectId() != null) {
            Project project = projectRepository.findById(req.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", req.getProjectId()));
            if (project != null) {
                ticket.setClient(project.getClient());
                ticket.setProject(project);
            }
        }
        if (req.getCategoryId() != null) {
            ticket.setCategory(categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", req.getCategoryId())));
        }
        TicketResponse response = toResponse(ticketRepository.save(ticket));
        log.info("Ticket updated: ticketNumber={}", ticket.getTicketNumber());
        return response;
    }

    @Override
    public void delete(UUID id) {
        Ticket ticket = findOrThrow(id);
        ticketRepository.deleteById(id);
        log.info("Ticket deleted: ticketNumber={}", ticket.getTicketNumber());
    }

    @Override
    @Transactional
    public TicketResponse assign(UUID id, AssignTicketRequest req) {
        Ticket ticket = findOrThrow(id);
        User assignee = userRepository.findById(req.getAssignedTo())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", req.getAssignedTo()));
        ticket.setAssignedTo(assignee);
        log.info("Ticket assigned: ticketNumber={}, assignedTo={}", ticket.getTicketNumber(), assignee.getEmail());
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    @Transactional
    public TicketResponse changeStatus(UUID id, ChangeStatusRequest req) {
        User currentUser = SecurityUtils.getCurrentUser();
        Ticket ticket = findOrThrow(id);
        String oldStatus = ticket.getStatus().name();
        ticket.setStatus(req.getStatus());
        ticket = ticketRepository.save(ticket);

        log.info("Ticket status changed: ticketNumber={}, {} -> {}, changedBy={}",
                ticket.getTicketNumber(), oldStatus, req.getStatus().name(), currentUser.getEmail());
        statusHistoryRepository.save(TicketStatusHistory.builder()
                .ticket(ticket)
                .oldStatus(oldStatus)
                .newStatus(req.getStatus().name())
                .changedBy(currentUser)
                .build());

        return toResponse(ticket);
    }

    private Ticket findOrThrow(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
    }

    private TicketResponse toResponse(Ticket t) {
        return TicketResponse.builder()
                .id(t.getId())
                .ticketNumber(t.getTicketNumber())
                .title(t.getTitle())
                .description(t.getDescription())
                .clientId(t.getClient() != null ? t.getClient().getId() : null)
                .clientName(t.getClient() != null ? t.getClient().getName() : null)
                .projectId(t.getProject() != null ? t.getProject().getId() : null)
                .projectName(t.getProject() != null ? t.getProject().getName() : null)
                .categoryId(t.getCategory() != null ? t.getCategory().getId() : null)
                .categoryName(t.getCategory() != null ? t.getCategory().getName() : null)
                .priority(t.getPriority())
                .status(t.getStatus())
                .createdById(t.getCreatedBy().getId())
                .createdByName(t.getCreatedBy().getFirstName() + " " + t.getCreatedBy().getLastName())
                .assignedToId(t.getAssignedTo() != null ? t.getAssignedTo().getId() : null)
                .assignedToName(t.getAssignedTo() != null ?
                        t.getAssignedTo().getFirstName() + " " + t.getAssignedTo().getLastName() : null)
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
