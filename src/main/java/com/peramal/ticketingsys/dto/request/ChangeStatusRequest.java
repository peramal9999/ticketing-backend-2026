package com.peramal.ticketingsys.dto.request;

import com.peramal.ticketingsys.entity.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeStatusRequest {
    @NotNull
    private TicketStatus status;
}
