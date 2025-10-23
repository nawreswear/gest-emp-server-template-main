package tn.iset.m2glnt.server.dto;

import java.time.LocalDateTime;

public record CalendarSlotDTO(
        int id,
        String description,
        TimeInterval timeInterval,
        int version
) {
    public LocalDateTime startTime() {
        return timeInterval.start();
    }

    public LocalDateTime endTime() {
        return timeInterval.end();
    }
}