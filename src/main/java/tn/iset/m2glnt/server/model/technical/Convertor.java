package tn.iset.m2glnt.server.model.technical;

import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.model.CalendarSlot;

public class Convertor {

    public static CalendarSlotDTO toCalendarSlotDTO(CalendarSlot slot) {
        return new CalendarSlotDTO(
                slot.getId(),
                slot.getNom(),
                slot.getDescription(),
                slot.getTimeBegin(),
                slot.getTimeEnd(), // CORRECTION : getTimeEnd() au lieu de getTime_end()
                slot.getVersion(),
                slot.getEnseignant() != null ? slot.getEnseignant().getId() : null,
                slot.getSalle() != null ? slot.getSalle().getId() : null
        );
    }

    public static CalendarSlot fromCalendarSlotDTO(CalendarSlotDTO dto) {
        CalendarSlot slot = new CalendarSlot();
        slot.setId(dto.id());
        slot.setNom(dto.nom());
        slot.setDescription(dto.description());
        slot.setTimeBegin(dto.timeBegin());
        slot.setTimeEnd(dto.timeEnd()); // CORRECTION : setTimeEnd() au lieu de setTime_end()
        slot.setVersion(dto.version());
        return slot;
    }
}