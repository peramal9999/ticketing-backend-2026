package com.peramal.ticketingsys.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AssignTicketRequest {

    @NotNull
    @Schema(type = "string", format = "uuid")
    private UUID assignedTo;
}
