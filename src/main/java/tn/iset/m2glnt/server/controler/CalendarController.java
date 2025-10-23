package tn.iset.m2glnt.server.controler;  // Note: Typo in package name ("controler" -> should be "controller")

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.model.CalendarSlot;
import tn.iset.m2glnt.server.model.technical.Convertor;
import tn.iset.m2glnt.server.repository.CalendarSlotRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/timeslots")
@CrossOrigin(origins = "*")
public class CalendarController {

    // Corrected: Changed from 'private' to package-private (default access) to allow test access
    @Autowired
    CalendarSlotRepository repository;  // Now accessible within the same package

    // GET /timeslots?start=2025-10-10&end=2025-10-20
    @GetMapping
    public List<CalendarSlotDTO> getSlotsBetween(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay().minusNanos(1);

        return repository.findAll().stream()
                .filter(slot -> !slot.getTime_begin().isBefore(startDateTime)
                        && !slot.getTime_end().isAfter(endDateTime))
                .map(Convertor::toCalendarSlotDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/slots/{id}")
    public ResponseEntity<CalendarSlotDTO> getSlotById(@PathVariable int id) {
        Optional<CalendarSlot> slot = repository.findById(id);
        return slot.map(s -> ResponseEntity.ok(Convertor.toCalendarSlotDTO(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/")
    public List<CalendarSlotDTO> getAllSlots() {
        return repository.findAll()
                .stream()
                .map(Convertor::toCalendarSlotDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/slots")
    public ResponseEntity<Integer> createSlot(@RequestBody CalendarSlotDTO dto) {
        try {
            CalendarSlot slot = Convertor.fromCalendarSlotDTO(dto);
            slot.setId(null); // laisser JPA générer l'id
            CalendarSlot saved = repository.save(slot);
            return ResponseEntity.ok(saved.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/slots/{id}")
    public ResponseEntity<Boolean> updateSlot(@PathVariable int id, @RequestBody CalendarSlotDTO dto) {
        Optional<CalendarSlot> optionalSlot = repository.findById(id);
        if (optionalSlot.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CalendarSlot existing = optionalSlot.get();
        if (dto.version() == existing.getVersion() + 1) {
            CalendarSlot updated = Convertor.fromCalendarSlotDTO(dto);
            updated.setId(id); // conserver le même ID
            repository.save(updated);
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(false);
    }

    @DeleteMapping("/slots/{id}")
    public ResponseEntity<Boolean> deleteSlot(@PathVariable int id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.ok(true);
    }
}