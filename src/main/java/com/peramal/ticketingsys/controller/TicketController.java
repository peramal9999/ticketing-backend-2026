package com.peramal.ticketingsys.controller;

import com.peramal.ticketingsys.dto.request.AssignTicketRequest;
import com.peramal.ticketingsys.dto.request.ChangeStatusRequest;
import com.peramal.ticketingsys.dto.request.CreateTicketRequest;
import com.peramal.ticketingsys.dto.request.UpdateTicketRequest;
import com.peramal.ticketingsys.dto.response.StatusHistoryResponse;
import com.peramal.ticketingsys.dto.response.TicketResponse;
import com.peramal.ticketingsys.entity.enums.Priority;
import com.peramal.ticketingsys.entity.enums.TicketStatus;
import com.peramal.ticketingsys.service.TicketService;
import com.peramal.ticketingsys.service.TicketStatusHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TicketStatusHistoryService historyService;

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAll(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) UUID projectId) {
        return ResponseEntity.ok(ticketService.getAll(status, priority, clientId, projectId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ticketSecurity.canViewTicket(#id)")
    public ResponseEntity<TicketResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ticketSecurity.canModifyTicket(#id)")
    public ResponseEntity<TicketResponse> update(@PathVariable UUID id,
                                                 @RequestBody UpdateTicketRequest request) {
        return ResponseEntity.ok(ticketService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<TicketResponse> assign(@PathVariable UUID id,
                                                 @Valid @RequestBody AssignTicketRequest request) {
        return ResponseEntity.ok(ticketService.assign(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@ticketSecurity.canModifyTicket(#id)")
    public ResponseEntity<TicketResponse> changeStatus(@PathVariable UUID id,
                                                       @Valid @RequestBody ChangeStatusRequest request) {
        return ResponseEntity.ok(ticketService.changeStatus(id, request));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("@ticketSecurity.canViewTicket(#id)")
    public ResponseEntity<List<StatusHistoryResponse>> getHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(historyService.getHistory(id));
    }
}
