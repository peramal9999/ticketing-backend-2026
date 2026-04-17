package com.peramal.ticketingsys.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateProjectRequest {

    @Schema(type = "string", format = "uuid")
    private UUID clientId;

    private String name;

    private String description;

    private Boolean isActive;
}
