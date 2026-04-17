package com.peramal.ticketingsys.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CommentResponse {
    private UUID id;
    private UUID ticketId;
    private UUID userId;
    private String userName;
    private String message;
    private Boolean isInternal;
    private LocalDateTime createdAt;
}
