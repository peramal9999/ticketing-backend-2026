package com.peramal.ticketingsys.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateProjectRequest {

    @NotNull
    @Schema(type = "string", format = "uuid")
    private UUID clientId;

    @NotBlank
    private String name;

    @NotBlank
    @Size(min = 5, max = 5, message = "Short code must be exactly 5 letters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Short code must contain letters only")
    private String shortCode;

    private String description;
    private Boolean isActive;
}
