package com.peramal.ticketingsys.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AttachmentResponse {
    private UUID id;
    private UUID ticketId;
    private UUID commentId;
    private String fileName;
    private String fileType;
    private Integer fileSize;
    private UUID uploadedById;
    private String uploadedByName;
    private LocalDateTime createdAt;
}
