package com.peramal.ticketingsys.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProjectResponse {
    private UUID id;
    private UUID clientId;
    private String clientName;
    private String name;
    private String shortCode;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
