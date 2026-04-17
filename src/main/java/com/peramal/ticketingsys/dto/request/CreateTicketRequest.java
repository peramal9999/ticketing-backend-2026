package com.peramal.ticketingsys.dto.request;

import com.peramal.ticketingsys.entity.enums.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateTicketRequest {

    @NotBlank
    private String title;

    private String description;

    @Schema(type = "string", format = "uuid")
    private UUID projectId;

    @Schema(type = "string", format = "uuid")
    private UUID categoryId;

    private Priority priority;

    @Schema(type = "string", format = "uuid")
    private UUID assignedTo;
}
