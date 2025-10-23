package tn.iset.m2glnt.server.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class CalendarSlotTest {

    @Test
    void getId() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);

        // Use full constructor with id and version
        CalendarSlot calendarSlot = new CalendarSlot(-1, startCM, endCM, "CM GLA", 1);
        assertEquals(-1, calendarSlot.getId());

        CalendarSlot calendarSlot1 = new CalendarSlot(3, startCM, endCM, "CM GLA", 1);
        assertEquals(3, calendarSlot1.getId());
    }

    @Test
    void getTime_begin() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);

        CalendarSlot calendarSlot = new CalendarSlot(-1, startCM, endCM, "CM GLA", 1);
        assertEquals(startCM, calendarSlot.getTime_begin());
    }

    @Test
    void getTime_end() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);

        CalendarSlot calendarSlot = new CalendarSlot(-1, startCM, endCM, "CM GLA", 1);
        assertEquals(endCM, calendarSlot.getTime_end());
    }

    @Test
    void getDescription() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);

        CalendarSlot calendarSlot = new CalendarSlot(-1, startCM, endCM, "CM GLA", 1);
        assertEquals("CM GLA", calendarSlot.getDescription());
    }

    @Test
    void setId() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);

        CalendarSlot calendarSlot = new CalendarSlot(-1, startCM, endCM, "CM GLA", 1);
        calendarSlot.setId(1);
        assertEquals(1, calendarSlot.getId());
    }

    @Test
    void getVersion() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);

        CalendarSlot calendarSlot = new CalendarSlot(-1, startCM, endCM, "CM GLA", 5);
        assertEquals(5, calendarSlot.getVersion());
    }
}
