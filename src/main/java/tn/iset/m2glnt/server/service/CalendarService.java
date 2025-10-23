package tn.iset.m2glnt.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.iset.m2glnt.server.dto.CalendarDTO;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;
import tn.iset.m2glnt.server.model.Calendar;
import tn.iset.m2glnt.server.model.CalendarSlot;
import tn.iset.m2glnt.server.model.DAO;
import tn.iset.m2glnt.server.model.technical.Convertor;
import tn.iset.m2glnt.server.repository.CalendarSlotRepository;
import tn.iset.m2glnt.server.repository.CalendarSlotRepository;
import tn.iset.m2glnt.server.storage.CalendarMemoryDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final Calendar calendar;
    @Autowired
     CalendarSlotRepository calendarRepository; // pour récupérer le Calendar

    @Autowired
     CalendarSlotRepository slotRepository; // pour sauvegarder les slots

    private final DAO<Integer, CalendarSlotDTO> dao;

    public CalendarService() {
        this.calendar = new Calendar();
        this.dao = new CalendarMemoryDAO(true);
        loadAll();
    }
    public List<CalendarSlotDTO> getSlotsByDate(LocalDate date) {
        return slotRepository.findByDate(date)
                .stream()
                .map(Convertor::toCalendarSlotDTO) //  Utilise ton convertisseur existant
                .collect(Collectors.toList());
    }

    private void loadAll() {
        List<CalendarSlotDTO> calendarSlots = dao.getAll();
        for (CalendarSlotDTO dto : calendarSlots) {
            calendar.addSlot(Convertor.fromCalendarSlotDTO(dto));
        }
    }

    public List<CalendarSlotDTO> getAllSlots() {
        List<CalendarSlot> slots = slotRepository.findAll(); // depuis MySQL
        return slots.stream().map(Convertor::toCalendarSlotDTO).toList();
    }

    public List<CalendarSlotDTO> getSlotsBetween(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay().minusNanos(1);

        List<CalendarSlot> slots = slotRepository.findAll().stream()
                .filter(slot -> !slot.getTime_begin().isBefore(startDateTime) &&
                        !slot.getTime_end().isAfter(endDateTime))
                .toList();

        return slots.stream().map(Convertor::toCalendarSlotDTO).toList();
    }

    public CalendarDTO getCalendarSlotsIn(TimeInterval timeInterval) {
        List<tn.iset.m2glnt.server.model.CalendarSlot> filteredSlots = calendar.getSlots()
                .stream()
                .filter(slot -> !slot.getTime_begin().isBefore(timeInterval.start()) &&
                        !slot.getTime_end().isAfter(timeInterval.end()))
                .toList();
        return new CalendarDTO(filteredSlots.stream().map(Convertor::toCalendarSlotDTO).toList());
    }

    public Optional<CalendarSlotDTO> getSlotById(int id) {
        Optional<CalendarSlot> slot = slotRepository.findById(id);
        return slot.map(Convertor::toCalendarSlotDTO);
    }

    public Integer createSlot(int calendarId, CalendarSlotDTO dto) {
        // Convert DTO to entity
        LocalDateTime start = dto.timeInterval().start();
        LocalDateTime end = dto.timeInterval().end();

        CalendarSlot slot = new CalendarSlot();
        slot.setTime_begin(start);
        slot.setTime_end(end);
        slot.setDescription(dto.description());
        slot.setVersion(dto.version());

        // Save to MySQL
        CalendarSlot saved = slotRepository.save(slot);

        // Optionally, add to your in-memory calendar object
        calendar.addSlot(saved);

        return saved.getId(); // return the generated ID
    }

    public boolean updateSlot(int id, CalendarSlotDTO dto) {
        Optional<CalendarSlot> existingOpt = slotRepository.findById(id);
        if (existingOpt.isEmpty()) return false;

        CalendarSlot existing = existingOpt.get();

        // Vérifier la version
        if (dto.version() == existing.getVersion() + 1) {
            existing.setDescription(dto.description());
            existing.setTime_begin(dto.timeInterval().start());
            existing.setTime_end(dto.timeInterval().end());
            existing.setVersion(dto.version());

            slotRepository.save(existing); // Update en DB
            calendar.removeSlotById(id);
            calendar.addSlot(existing); // update mémoire
            return true;
        }
        return false;
    }

    public boolean deleteSlot(int id) {
        Optional<CalendarSlot> existing = slotRepository.findById(id);
        if (existing.isEmpty()) return false;

        // Supprimer depuis MySQL
        slotRepository.deleteById(id);

        // Supprimer depuis le calendrier en mémoire
        calendar.removeSlotById(id);

        // Supprimer depuis le DAO si tu veux garder la cohérence mémoire
        dao.delete(Convertor.toCalendarSlotDTO(existing.get()));

        return true;
    }

    /*public List<CalendarSlotDTO> getAllSlots() {
        // Récupérer tous les slots depuis le DAO
        return dao.getAll();
    }*/

}