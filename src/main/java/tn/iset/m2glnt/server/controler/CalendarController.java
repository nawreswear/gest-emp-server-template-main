package tn.iset.m2glnt.server.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.model.CalendarSlot;
import tn.iset.m2glnt.server.model.Enseignant;
import tn.iset.m2glnt.server.model.Salle;
import tn.iset.m2glnt.server.model.technical.Convertor;
import tn.iset.m2glnt.server.repository.CalendarSlotRepository;
import tn.iset.m2glnt.server.repository.EnseignantRepository;
import tn.iset.m2glnt.server.repository.SalleRepository;
import tn.iset.m2glnt.server.service.EnseignantService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/timeslots")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class CalendarController {

    @Autowired
    private CalendarSlotRepository repository;

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private SalleRepository salleRepository;
    @Autowired
    EnseignantService enseignantService;

    // ✅ CORRIGÉ : @RequestParam avec nom explicite
    @GetMapping
    public ResponseEntity<?> getSlotsBetween(
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            System.out.println("=== BACKEND GET SLOTS ===");
            System.out.println("Request: startDate=" + startDate + ", endDate=" + endDate);

            List<CalendarSlot> slots;

            if (startDate != null && endDate != null) {
                if (startDate.isAfter(endDate)) {
                    return ResponseEntity.badRequest().body("La date de début doit être avant la date de fin");
                }

                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
                slots = repository.findByTimeBeginBetween(startDateTime, endDateTime);
            } else {
                slots = repository.findAll();
            }

            List<CalendarSlotDTO> result = slots.stream()
                    .map(Convertor::toCalendarSlotDTO)
                    .collect(Collectors.toList());

            System.out.println("✅ Response: " + result.size() + " slots");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("❌ Error in getSlotsBetween: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching slots: " + e.getMessage());
        }
    }

    // ✅ CORRIGÉ : @RequestParam avec nom explicite
    @GetMapping("/by-date")
    public ResponseEntity<?> getSlotsByDate(
            @RequestParam(name = "date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            List<CalendarSlot> slots = repository.findByDate(date);
            List<CalendarSlotDTO> result = slots.stream()
                    .map(Convertor::toCalendarSlotDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching slots by date: " + e.getMessage());
        }
    }

    // ✅ CORRIGÉ : Ajout de @PathVariable avec nom explicite
    @GetMapping("/{id}")
    public ResponseEntity<?> getSlotById(@PathVariable("id") int id) {
        try {
            Optional<CalendarSlot> slot = repository.findById(id);
            if (slot.isPresent()) {
                return ResponseEntity.ok(Convertor.toCalendarSlotDTO(slot.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createSlot(@RequestBody CalendarSlotDTO dto) {
        try {
            System.out.println("=== BACKEND CREATE SLOT ===");
            System.out.println("Received DTO: " + dto);

            // VALIDATION
            if (dto.nom() == null || dto.nom().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Le nom est obligatoire");
            }

            if (dto.timeBegin() == null || dto.timeEnd() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Les dates de début et fin sont obligatoires");
            }

            if (dto.timeBegin().isAfter(dto.timeEnd())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La date de début doit être avant la date de fin");
            }

            CalendarSlot slot = new CalendarSlot();
            slot.setNom(dto.nom());
            slot.setDescription(dto.description());
            slot.setTimeBegin(dto.timeBegin());
            slot.setTimeEnd(dto.timeEnd());
            slot.setVersion(dto.version());

            // Associer enseignant si fourni
            if (dto.enseignantId() != null) {
                Optional<Enseignant> enseignant = enseignantRepository.findById(dto.enseignantId());
                if (enseignant.isPresent()) {
                    slot.setEnseignant(enseignant.get());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Enseignant non trouvé avec l'ID: " + dto.enseignantId());
                }
            }

            // Associer salle si fournie
            if (dto.salleId() != null) {
                Optional<Salle> salle = salleRepository.findById(dto.salleId());
                if (salle.isPresent()) {
                    slot.setSalle(salle.get());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Salle non trouvée avec l'ID: " + dto.salleId());
                }
            }

            CalendarSlot saved = repository.save(slot);
            System.out.println("✅ Slot created with ID: " + saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());

        } catch (Exception e) {
            System.err.println("❌ Error creating slot: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating slot: " + e.getMessage());
        }
    }

    // ✅ CORRIGÉ : Ajout de @PathVariable avec nom explicite
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSlot(@PathVariable("id") int id, @RequestBody CalendarSlotDTO dto) {
        try {
            Optional<CalendarSlot> optionalSlot = repository.findById(id);
            if (optionalSlot.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            CalendarSlot existing = optionalSlot.get();

            // Validation de version
            if (dto.version() < existing.getVersion()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Conflit de version. Version courante: " + existing.getVersion());
            }

            // Mise à jour des champs
            existing.setNom(dto.nom());
            existing.setDescription(dto.description());
            existing.setTimeBegin(dto.timeBegin());
            existing.setTimeEnd(dto.timeEnd());
            existing.setVersion(dto.version());

            // Mise à jour des relations
            if (dto.enseignantId() != null) {
                Optional<Enseignant> enseignant = enseignantRepository.findById(dto.enseignantId());
                enseignant.ifPresent(existing::setEnseignant);
            } else {
                existing.setEnseignant(null);
            }

            if (dto.salleId() != null) {
                Optional<Salle> salle = salleRepository.findById(dto.salleId());
                salle.ifPresent(existing::setSalle);
            } else {
                existing.setSalle(null);
            }

            CalendarSlot updated = repository.save(existing);
            return ResponseEntity.ok(Convertor.toCalendarSlotDTO(updated));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating slot: " + e.getMessage());
        }
    }

    // ✅ CORRIGÉ : Ajout de @PathVariable avec nom explicite
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSlot(@PathVariable("id") int id) {
        try {
            if (!repository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            repository.deleteById(id);
            return ResponseEntity.ok("Slot supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting slot: " + e.getMessage());
        }
    }

    @DeleteMapping("/enseignant/delete/{id}")
    public ResponseEntity<?> deleteEnseignant(@PathVariable Long id) {
        try {
            enseignantService.deleteEnseignant(id);

            return ResponseEntity.ok().body(Map.of(
                    "message", "Enseignant supprimé avec succès",
                    "timestamp", Instant.now()
            ));

        } catch (RuntimeException e) {
            // Logger l'erreur pour le debugging
            System.err.println("❌ Erreur suppression enseignant ID " + id + ": " + e.getMessage());
            e.printStackTrace();

            // Retourner une réponse claire
            String errorMessage = e.getMessage();
            int statusCode = 500;

            if (errorMessage.contains("créneaux horaires") ||
                    errorMessage.contains("référencé") ||
                    errorMessage.contains("associé")) {
                statusCode = 409; // CONFLICT
            } else if (errorMessage.contains("non trouvé")) {
                statusCode = 404; // NOT FOUND
            }

            return ResponseEntity.status(statusCode)
                    .body(Map.of(
                            "message", "Erreur lors de la suppression",
                            "error", errorMessage,
                            "timestamp", Instant.now(),
                            "path", "/enseignant/delete/" + id
                    ));

        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue suppression enseignant ID " + id + ": " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Erreur interne du serveur",
                            "error", "Une erreur inattendue s'est produite",
                            "timestamp", Instant.now(),
                            "path", "/enseignant/delete/" + id
                    ));
        }
    }
    // Dans CalendarController.java - Ajoutez ces méthodes

    @DeleteMapping("/salle/delete/{id}")
    public ResponseEntity<?> deleteSalle(@PathVariable Long id) {
        try {
            // Vérifier si la salle existe
            Optional<Salle> salleOpt = salleRepository.findById(id);
            if (salleOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "message", "Salle non trouvée avec l'ID: " + id,
                                "timestamp", Instant.now()
                        ));
            }

            Salle salle = salleOpt.get();
            System.out.println("🗑️ Tentative de suppression de la salle ID: " + id + " - Nom: " + salle.getNom());

            // Vérifier s'il y a des slots utilisant cette salle
            long slotsCount = repository.countBySalleId(id);
            System.out.println("📊 " + slotsCount + " slots utilisent cette salle");

            if (slotsCount > 0) {
                // Dissocier les slots (mettre la salle à null)
                int slotsUpdated = repository.setSalleToNullBySalleId(id);
                System.out.println("🔄 " + slotsUpdated + " slots dissociés de la salle");
            }

            // Supprimer la salle
            salleRepository.deleteById(id);
            System.out.println("✅ Salle ID " + id + " supprimée avec succès");

            return ResponseEntity.ok().body(Map.of(
                    "message", "Salle supprimée avec succès",
                    "slotsDissociated", slotsCount,
                    "timestamp", Instant.now()
            ));

        } catch (RuntimeException e) {
            System.err.println("❌ Erreur suppression salle ID " + id + ": " + e.getMessage());
            e.printStackTrace();

            String errorMessage = e.getMessage();
            int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();

            if (errorMessage.contains("non trouvée")) {
                statusCode = HttpStatus.NOT_FOUND.value();
            } else if (errorMessage.contains("référencé") || errorMessage.contains("associé")) {
                statusCode = HttpStatus.CONFLICT.value();
            }

            return ResponseEntity.status(statusCode)
                    .body(Map.of(
                            "message", "Erreur lors de la suppression de la salle",
                            "error", errorMessage,
                            "timestamp", Instant.now(),
                            "path", "/salle/delete/" + id
                    ));

        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue suppression salle ID " + id + ": " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Erreur interne du serveur",
                            "error", "Une erreur inattendue s'est produite",
                            "timestamp", Instant.now(),
                            "path", "/salle/delete/" + id
                    ));
        }
    }

    @GetMapping("/salle/{id}/can-delete")
    public ResponseEntity<?> canDeleteSalle(@PathVariable Long id) {
        try {
            boolean hasSlots = repository.existsBySalleId(id);

            return ResponseEntity.ok().body(Map.of(
                    "canDelete", true, // Toujours true car on dissocie les slots
                    "hasAssociatedSlots", hasSlots,
                    "message", hasSlots ?
                            "La salle a des créneaux associés qui seront dissociés" :
                            "La salle peut être supprimée sans impact",
                    "slotsCount", hasSlots ? repository.countBySalleId(id) : 0
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "canDelete", false,
                            "error", e.getMessage()
                    ));
        }
    }
    @GetMapping("/{id}/can-delete")
    public ResponseEntity<?> canDeleteEnseignant(@PathVariable Long id) {
        try {
            boolean hasSlots = repository.existsByEnseignantId(id);

            return ResponseEntity.ok().body(Map.of(
                    "canDelete", !hasSlots,
                    "hasAssociatedSlots", hasSlots,
                    "message", hasSlots ?
                            "L'enseignant a des créneaux horaires associés" :
                            "L'enseignant peut être supprimé"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "canDelete", false,
                            "error", e.getMessage()
                    ));
        }
    }
}