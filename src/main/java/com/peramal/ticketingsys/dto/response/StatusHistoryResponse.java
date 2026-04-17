package com.peramal.ticketingsys.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StatusHistoryResponse {
    private UUID id;
    private UUID ticketId;
    private String oldStatus;
    private String newStatus;
    private UUID changedById;
    private String changedByName;
    private LocalDateTime createdAt;
}
