package tn.iset.m2glnt.server.model.technical;

import tn.iset.m2glnt.server.dto.CalendarDTO;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;
import tn.iset.m2glnt.server.model.Calendar;
import tn.iset.m2glnt.server.model.CalendarSlot;

import java.util.List;
import java.util.stream.Collectors;

public class Convertor {

    public static CalendarSlotDTO toCalendarSlotDTO(CalendarSlot slot) {
        if (slot == null) return null;
        return new CalendarSlotDTO(
                slot.getId(),
                slot.getDescription(),
                new TimeInterval(slot.getTime_begin(), slot.getTime_end()),
                slot.getVersion()
        );
    }

    public static CalendarSlot fromCalendarSlotDTO(CalendarSlotDTO dto) {
        if (dto == null) return null;
        CalendarSlot slot = new CalendarSlot();
        slot.setId(dto.id());
        slot.setTime_begin(dto.timeInterval().start());
        slot.setTime_end(dto.timeInterval().end());
        slot.setDescription(dto.description());
        slot.setVersion(dto.version());
        return slot;
    }

    public static CalendarDTO toCalendarDTO(Calendar calendar) {
        if (calendar == null) return null;
        List<CalendarSlotDTO> dtoList = calendar.getSlots().stream()
                .map(Convertor::toCalendarSlotDTO)
                .collect(Collectors.toList());
        return new CalendarDTO(dtoList);
    }

    public static Calendar fromCalendarDTO(CalendarDTO dto) {
        if (dto == null) return null;
        Calendar calendar = new Calendar();
        dto.calendarSlots().stream()
                .map(Convertor::fromCalendarSlotDTO)
                .forEach(calendar::addSlot);
        return calendar;
    }
}