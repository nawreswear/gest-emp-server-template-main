package tn.iset.m2glnt.server.model.technical;

import tn.iset.m2glnt.server.model.Calendar;
import tn.iset.m2glnt.server.model.CalendarSlot;
import tn.iset.m2glnt.server.dto.CalendarDTO;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConvertorTest {

    @Test
    void testToCalendarSlotDTO() {
        // Arrange
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 10, 0, 0);
        CalendarSlot calendarSlot = new CalendarSlot(1, startCM, endCM, "CM GLA", 1);

        // Act
        CalendarSlotDTO calendarSlotDTO = Convertor.toCalendarSlotDTO(calendarSlot);

        // Assert
        assertEquals(calendarSlot.getId(), calendarSlotDTO.id());
        assertEquals(calendarSlot.getDescription(), calendarSlotDTO.description());
        assertEquals(calendarSlot.getTime_begin(), calendarSlotDTO.timeInterval().start());
        assertEquals(calendarSlot.getTime_end(), calendarSlotDTO.timeInterval().end());
        assertEquals(calendarSlot.getVersion(), calendarSlotDTO.version());
    }

    @Test
    void testFromCalendarSlotDTO() {
        // Arrange
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 10, 0, 0);
        CalendarSlotDTO calendarSlotDTO = new CalendarSlotDTO(-1, "CM GLA", new TimeInterval(startCM, endCM), 1);

        // Act
        CalendarSlot calendarSlot = Convertor.fromCalendarSlotDTO(calendarSlotDTO);

        // Assert
        assertEquals(calendarSlotDTO.id(), calendarSlot.getId());
        assertEquals(calendarSlotDTO.description(), calendarSlot.getDescription());
        assertEquals(calendarSlotDTO.timeInterval().start(), calendarSlot.getTime_begin());
        assertEquals(calendarSlotDTO.timeInterval().end(), calendarSlot.getTime_end());
        assertEquals(calendarSlotDTO.version(), calendarSlot.getVersion());
    }

    @Test
    void testToCalendarDTO() {
        // Arrange
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 10, 0, 0);
        CalendarSlot calendarSlot = new CalendarSlot(1, startCM, endCM, "CM GLA", 1);
        Calendar calendar = new Calendar();
        calendar.addSlot(calendarSlot);

        // Act
        CalendarDTO calendarDTO = Convertor.toCalendarDTO(calendar);

        // Assert
        assertEquals(1, calendarDTO.calendarSlots().size());
        CalendarSlotDTO firstSlotDTO = calendarDTO.calendarSlots().iterator().next();
        assertEquals(startCM, firstSlotDTO.timeInterval().start());
        assertEquals(endCM, firstSlotDTO.timeInterval().end());
    }

    @Test
    void testFromCalendarDTO() {
        // Arrange
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 10, 0, 0);

        // CORRECTION : Créer directement le DTO
        CalendarSlotDTO calendarSlotDTO = new CalendarSlotDTO(1, "CM GLA", new TimeInterval(startCM, endCM), 1);

        // CORRECTION : Créer le CalendarDTO avec la liste de DTOs
        CalendarDTO calendarDTO = new CalendarDTO(List.of(calendarSlotDTO));

        // Act
        Calendar calendar = Convertor.fromCalendarDTO(calendarDTO);

        // Assert
        assertEquals(1, calendar.getSlots().size());
        CalendarSlot firstSlot = calendar.getSlots().iterator().next();
        assertEquals(startCM, firstSlot.getTime_begin());
        assertEquals(endCM, firstSlot.getTime_end());
    }
}