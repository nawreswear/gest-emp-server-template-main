package tn.iset.m2glnt.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.iset.m2glnt.server.dto.CalendarDTO;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;
import tn.iset.m2glnt.server.model.*;
import tn.iset.m2glnt.server.model.technical.Convertor;
import tn.iset.m2glnt.server.repository.CalendarRepository;
import tn.iset.m2glnt.server.repository.CalendarSlotRepository;
import tn.iset.m2glnt.server.repository.EnseignantRepository;
import tn.iset.m2glnt.server.repository.SalleRepository;
import tn.iset.m2glnt.server.storage.CalendarMemoryDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CalendarService {

    private final Calendar calendar;
    private final DAO<Integer, CalendarSlotDTO> dao;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private CalendarSlotRepository slotRepository;

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private SalleRepository salleRepository;

    public CalendarService() {
        this.calendar = new Calendar();
        this.dao = new CalendarMemoryDAO(true);
        loadAll();
    }

    private void loadAll() {
        List<CalendarSlotDTO> calendarSlots = dao.getAll();
        for (CalendarSlotDTO dto : calendarSlots) {
            calendar.addSlot(Convertor.fromCalendarSlotDTO(dto));
        }
    }

    // ✅ Récupérer tous les slots
    public List<CalendarSlotDTO> getAllSlots() {
        List<CalendarSlot> slots = slotRepository.findAll();
        return slots.stream()
                .map(Convertor::toCalendarSlotDTO)
                .collect(Collectors.toList());
    }

    // ✅ Récupérer les slots par date
    public List<CalendarSlotDTO> getSlotsByDate(LocalDate date) {
        return slotRepository.findByDate(date)
                .stream()
                .map(Convertor::toCalendarSlotDTO)
                .collect(Collectors.toList());
    }

    // ✅ Récupérer les slots entre deux dates
    public List<CalendarSlotDTO> getSlotsBetween(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay().minusNanos(1);

        List<CalendarSlot> slots = slotRepository.findByTimeBeginBetween(startDateTime, endDateTime);

        return slots.stream()
                .map(Convertor::toCalendarSlotDTO)
                .collect(Collectors.toList());
    }

    // ✅ Récupérer un slot par ID
    public Optional<CalendarSlotDTO> getSlotById(int id) {
        Optional<CalendarSlot> slot = slotRepository.findById(id);
        return slot.map(Convertor::toCalendarSlotDTO);
    }

    // ✅ Créer un nouveau slot
    public Integer createSlot(int calendarId, CalendarSlotDTO dto) {
        // Validation des données
        validateCalendarSlotDTO(dto);

        CalendarSlot slot = new CalendarSlot();
        slot.setNom(dto.nom());
        slot.setTimeBegin(dto.timeBegin());
        slot.setTimeEnd(dto.timeEnd());
        slot.setDescription(dto.description());
        slot.setVersion(dto.version());

        // Associer un enseignant si présent
        if (dto.enseignantId() != null) {
            Enseignant enseignant = enseignantRepository.findById(dto.enseignantId())
                    .orElseThrow(() -> new RuntimeException("Enseignant non trouvé avec l'ID: " + dto.enseignantId()));
            slot.setEnseignant(enseignant);
        }

        // Associer une salle si présente
        if (dto.salleId() != null) {
            Salle salle = salleRepository.findById(dto.salleId())
                    .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'ID: " + dto.salleId()));
            slot.setSalle(salle);
        }

        CalendarSlot saved = slotRepository.save(slot);

        // Mettre à jour le calendrier en mémoire
        calendar.addSlot(saved);
        dao.create(Convertor.toCalendarSlotDTO(saved));

        return saved.getId();
    }

    // ✅ Mettre à jour un slot
    public boolean updateSlot(int id, CalendarSlotDTO dto) {
        // Validation des données
        validateCalendarSlotDTO(dto);

        Optional<CalendarSlot> existingOpt = slotRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return false;
        }

        CalendarSlot existing = existingOpt.get();

        // Vérification de version optimiste
        if (dto.version() != existing.getVersion() + 1) {
            return false;
        }

        // Mise à jour des champs
        existing.setNom(dto.nom());
        existing.setDescription(dto.description());
        existing.setTimeBegin(dto.timeBegin());
        existing.setTimeEnd(dto.timeEnd());
        existing.setVersion(dto.version());

        // Mise à jour des relations
        if (dto.enseignantId() != null) {
            Enseignant enseignant = enseignantRepository.findById(dto.enseignantId())
                    .orElseThrow(() -> new RuntimeException("Enseignant non trouvé avec l'ID: " + dto.enseignantId()));
            existing.setEnseignant(enseignant);
        } else {
            existing.setEnseignant(null);
        }

        if (dto.salleId() != null) {
            Salle salle = salleRepository.findById(dto.salleId())
                    .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'ID: " + dto.salleId()));
            existing.setSalle(salle);
        } else {
            existing.setSalle(null);
        }

        CalendarSlot updated = slotRepository.save(existing);

        // Mettre à jour le calendrier en mémoire
        calendar.removeSlotById(id);
        calendar.addSlot(updated);
        dao.update(Convertor.toCalendarSlotDTO(updated));

        return true;
    }
    // Pour l'OPTION 2: Dissocier les slots (mettre la salle à null)

    // ✅ Supprimer un slot
    public boolean deleteSlot(int id) {
        Optional<CalendarSlot> existing = slotRepository.findById(id);
        if (existing.isEmpty()) {
            return false;
        }

        slotRepository.deleteById(id);
        calendar.removeSlotById(id);
        dao.delete(Convertor.toCalendarSlotDTO(existing.get()));

        return true;
    }

    // ✅ Récupérer les slots dans un intervalle de temps
    public CalendarDTO getCalendarSlotsIn(TimeInterval timeInterval) {
        List<CalendarSlot> filteredSlots = calendar.getSlots()
                .stream()
                .filter(slot -> !slot.getTimeBegin().isBefore(timeInterval.start()) &&
                        !slot.getTimeEnd().isAfter(timeInterval.end()))
                .collect(Collectors.toList());

        return new CalendarDTO(filteredSlots.stream()
                .map(Convertor::toCalendarSlotDTO)
                .collect(Collectors.toList()));
    }

    // ✅ Validation des données
    private void validateCalendarSlotDTO(CalendarSlotDTO dto) {
        if (dto.nom() == null || dto.nom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }

        if (dto.timeBegin() == null || dto.timeEnd() == null) {
            throw new IllegalArgumentException("Les dates de début et fin sont obligatoires");
        }

        if (dto.timeBegin().isAfter(dto.timeEnd())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        if (dto.version() < 0) {
            throw new IllegalArgumentException("La version ne peut pas être négative");
        }
    }

    // ✅ Vérifier les conflits de créneaux
    public boolean hasSlotConflict(LocalDateTime start, LocalDateTime end, Integer excludeSlotId) {
        List<CalendarSlot> conflictingSlots = slotRepository.findAll().stream()
                .filter(slot -> !slot.getId().equals(excludeSlotId))
                .filter(slot -> isTimeOverlap(slot.getTimeBegin(), slot.getTimeEnd(), start, end))
                .collect(Collectors.toList());

        return !conflictingSlots.isEmpty();
    }

    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1,
                                  LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}