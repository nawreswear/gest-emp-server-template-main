package tn.iset.m2glnt.server.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalendarTest {

    @Test
    void addSlot() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 10, 0, 0);

        // Use full constructor: id = 1, version = 1
        CalendarSlot calendarSlot = new CalendarSlot(1, startCM, endCM, "CM GLA", 1);
        Calendar calendar = new Calendar();
        calendar.addSlot(calendarSlot);

        // Use iterator to get first element
        CalendarSlot firstSlot = calendar.getSlots().iterator().next();

        assertEquals(calendarSlot, firstSlot);
        assertEquals(startCM, firstSlot.getTime_begin());
        assertEquals(endCM, firstSlot.getTime_end());
        assertEquals("CM GLA", firstSlot.getDescription());
        assertEquals(1, firstSlot.getVersion());
    }

    @Test
    void getSlots() {
        Calendar calendar = new Calendar();
        assertTrue(calendar.getSlots().isEmpty());

        // Add a slot and test size
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 10, 0, 0);
        CalendarSlot calendarSlot = new CalendarSlot(1, startCM, endCM, "CM GLA", 1);
        calendar.addSlot(calendarSlot);

        assertEquals(1, calendar.getSlots().size());
    }
}
