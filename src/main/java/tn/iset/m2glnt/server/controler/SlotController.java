package tn.iset.m2glnt.server.controler;

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

    // Récupérer un slot par ID
    @GetMapping("/slot/{id}")
    public ResponseEntity<CalendarSlotDTO> getSlot(@PathVariable int id) {
        Optional<CalendarSlotDTO> slotOpt = calendarService.getSlotById(id);

        return slotOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // Récupérer tous les slots d'une date précise
    @GetMapping("/by-date")
    public ResponseEntity<?> getSlotsByExactDate(@RequestParam String date) {
        try {
            LocalDate selectedDate = LocalDate.parse(date);
            List<CalendarSlotDTO> slots = calendarService.getSlotsByDate(selectedDate);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
        }
    }

    // Créer un slot pour un calendrier donné
    @PostMapping("/{id}")
    public ResponseEntity<?> createSlot(@PathVariable int id, @RequestBody CalendarSlotDTO slot) {
        try {
            Integer newId = calendarService.createSlot(id, slot);
            return ResponseEntity.status(201).body("Slot créé avec l'ID : " + newId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la création : " + e.getMessage());
        }
    }

    // Mettre à jour un slot
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSlot(@PathVariable int id, @RequestBody CalendarSlotDTO slot) {
        boolean updated = calendarService.updateSlot(id, slot);
        if (updated) {
            return ResponseEntity.ok("Slot mis à jour avec succès");
        } else {
            return ResponseEntity.status(409).body("Échec de mise à jour (version incorrecte ou slot non trouvé)");
        }
    }

    // Supprimer un slot
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSlot(@PathVariable int id) {
        boolean deleted = calendarService.deleteSlot(id);
        if (deleted) {
            return ResponseEntity.ok("Slot supprimé avec succès");
        } else {
            return ResponseEntity.status(404).body("Slot non trouvé");
        }
    }

    // Récupérer les slots entre deux dates OU tous les slots si aucun paramètre
    @GetMapping
    public ResponseEntity<?> getSlots(@RequestParam(required = false) String start,
                                      @RequestParam(required = false) String end) {
        try {
            List<CalendarSlotDTO> slots;
            if (start != null && end != null) {
                LocalDate startDate = LocalDate.parse(start);
                LocalDate endDate = LocalDate.parse(end);
                slots = calendarService.getSlotsBetween(startDate, endDate);
            } else {
                // Aucun paramètre → récupérer tous les slots
                slots = calendarService.getAllSlots();
            }
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur : " + e.getMessage());
        }
    }


}