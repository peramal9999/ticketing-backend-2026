package com.peramal.ticketingsys.security;

import com.peramal.ticketingsys.entity.User;
import com.peramal.ticketingsys.entity.enums.Role;
import com.peramal.ticketingsys.repository.TicketRepository;
import com.peramal.ticketingsys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("ticketSecurity")
@RequiredArgsConstructor
public class TicketSecurity {

    private final TicketRepository ticketRepository;

    public boolean canViewTicket(UUID ticketId) {
        User user = SecurityUtils.getCurrentUser();
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPPORT) {
            return true;
        }
        return ticketRepository.findById(ticketId)
                .map(t -> t.getCreatedBy().getId().equals(user.getId()))
                .orElse(false);
    }

    public boolean canModifyTicket(UUID ticketId) {
        User user = SecurityUtils.getCurrentUser();
        if (user.getRole() == Role.ADMIN) return true;
        if (user.getRole() == Role.SUPPORT) {
            return ticketRepository.findById(ticketId)
                    .map(t -> t.getAssignedTo() != null && t.getAssignedTo().getId().equals(user.getId()))
                    .orElse(false);
        }
        return ticketRepository.findById(ticketId)
                .map(t -> t.getCreatedBy().getId().equals(user.getId()))
                .orElse(false);
    }

    public boolean isAdminOrSupport() {
        User user = SecurityUtils.getCurrentUser();
        return user.getRole() == Role.ADMIN || user.getRole() == Role.SUPPORT;
    }

    public boolean isTicketOwnerOrAdminOrSupport(UUID ticketId) {
        User user = SecurityUtils.getCurrentUser();
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPPORT) return true;
        return ticketRepository.findById(ticketId)
                .map(t -> t.getCreatedBy().getId().equals(user.getId()))
                .orElse(false);
    }
}
