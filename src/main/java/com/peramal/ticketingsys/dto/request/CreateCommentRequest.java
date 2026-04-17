package com.peramal.ticketingsys.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotBlank
    private String message;

    private Boolean isInternal = false;
}
