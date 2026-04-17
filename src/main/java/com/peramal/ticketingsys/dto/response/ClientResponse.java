package com.peramal.ticketingsys.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ClientResponse {
    private UUID id;
    private String name;
    private String contactEmail;
    private LocalDateTime createdAt;
}
