package com.peramal.ticketingsys.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateClientRequest {
    @NotBlank
    private String name;

    @Email
    private String contactEmail;
}
