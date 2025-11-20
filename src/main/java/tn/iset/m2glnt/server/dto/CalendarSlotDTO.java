package tn.iset.m2glnt.server.dto;

import java.time.LocalDateTime;

public record CalendarSlotDTO(
        int id,
        String nom,
        String description,
        LocalDateTime timeBegin,
        LocalDateTime timeEnd,
        int version,
        Long enseignantId,
        Long salleId
) {}