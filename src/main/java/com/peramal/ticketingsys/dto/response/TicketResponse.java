package com.peramal.ticketingsys.dto.response;

import com.peramal.ticketingsys.entity.enums.Priority;
import com.peramal.ticketingsys.entity.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TicketResponse {
    private UUID id;
    private String ticketNumber;
    private String title;
    private String description;
    private UUID clientId;
    private String clientName;
    private UUID projectId;
    private String projectName;
    private UUID categoryId;
    private String categoryName;
    private Priority priority;
    private TicketStatus status;
    private UUID createdById;
    private String createdByName;
    private UUID assignedToId;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
