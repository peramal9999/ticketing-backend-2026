package com.peramal.ticketingsys.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TicketNumberGenerator {

    private final AtomicInteger sequence = new AtomicInteger(1);

    /**
     * Generate a ticket number using the project short code as prefix.
     * e.g. "PROJ-00042"
     * Falls back to "TKT-00042" when no project is linked.
     */
    public String generate(String prefix) {
        int seq = sequence.getAndIncrement();
        String p = (prefix != null && !prefix.isBlank()) ? prefix.toUpperCase() : "TKT";
        return String.format("%s-%05d", p, seq);
    }
}
