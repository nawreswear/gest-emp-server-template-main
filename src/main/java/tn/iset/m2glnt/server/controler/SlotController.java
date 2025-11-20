/*package tn.iset.m2glnt.server.controler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.service.CalendarService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/slots")
public class SlotController {

    private final CalendarService calendarService;

    public SlotController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    // ✅ CORRIGÉ : @RequestParam avec nom explicite
    @GetMapping
    public ResponseEntity<?> getSlots(
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end) {

        try {
            List<CalendarSlotDTO> slots;
            if (start != null && end != null) {
                LocalDate startDate = LocalDate.parse(start);
                LocalDate endDate = LocalDate.parse(end);
                slots = calendarService.getSlotsBetween(startDate, endDate);
            } else {
                slots = calendarService.getAllSlots();
            }
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur : " + e.getMessage());
        }
    }

    // ✅ CORRIGÉ : @RequestParam avec nom explicite
    @GetMapping("/by-date")
    public ResponseEntity<?> getSlotsByExactDate(
            @RequestParam(name = "date") String date) {

        try {
            LocalDate selectedDate = LocalDate.parse(date);
            List<CalendarSlotDTO> slots = calendarService.getSlotsByDate(selectedDate);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
        }
    }

    // ✅ CORRIGÉ : @PathVariable avec nom explicite
    @GetMapping("/{id}")
    public ResponseEntity<?> getSlot(@PathVariable("id") int id) {
        try {
            Optional<CalendarSlotDTO> slotOpt = calendarService.getSlotById(id);
            return slotOpt
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    // ✅ CORRIGÉ : @PathVariable avec nom explicite
    @PostMapping("/calendar/{calendarId}")
    public ResponseEntity<?> createSlot(@PathVariable("calendarId") int calendarId, @RequestBody CalendarSlotDTO slot) {
        try {
            Integer newId = calendarService.createSlot(calendarId, slot);
            return ResponseEntity.status(201).body(newId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la création : " + e.getMessage());
        }
    }

    // ✅ CORRIGÉ : @PathVariable avec nom explicite
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSlot(@PathVariable("id") int id, @RequestBody CalendarSlotDTO slot) {
        try {
            boolean updated = calendarService.updateSlot(id, slot);
            if (updated) {
                return ResponseEntity.ok("Slot mis à jour avec succès");
            } else {
                return ResponseEntity.status(409).body("Échec de mise à jour (version incorrecte ou slot non trouvé)");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    // ✅ CORRIGÉ : @PathVariable avec nom explicite
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSlot(@PathVariable("id") int id) {
        try {
            boolean deleted = calendarService.deleteSlot(id);
            if (deleted) {
                return ResponseEntity.ok("Slot supprimé avec succès");
            } else {
                return ResponseEntity.status(404).body("Slot non trouvé");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSlots() {
        try {
            List<CalendarSlotDTO> slots = calendarService.getAllSlots();
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur : " + e.getMessage());
        }
    }
}*/