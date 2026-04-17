package com.peramal.ticketingsys.dto.request;

import com.peramal.ticketingsys.entity.enums.Role;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;

    @Email
    private String email;

    private Role role;
    private Boolean isActive;
}
