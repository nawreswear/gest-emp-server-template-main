package tn.iset.m2glnt.server.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.iset.m2glnt.server.controler.CalendarController;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.model.CalendarSlot;
import tn.iset.m2glnt.server.model.Enseignant;
import tn.iset.m2glnt.server.model.Salle;
import tn.iset.m2glnt.server.model.technical.Convertor;
import tn.iset.m2glnt.server.repository.CalendarSlotRepository;
import tn.iset.m2glnt.server.repository.EnseignantRepository;
import tn.iset.m2glnt.server.repository.SalleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CalendarControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CalendarSlotRepository calendarSlotRepository;

    @Mock
    private EnseignantRepository enseignantRepository;

    @Mock
    private SalleRepository salleRepository;

    @InjectMocks
    private CalendarController calendarController;

    private ObjectMapper objectMapper;
    private CalendarSlot testSlot;
    private CalendarSlotDTO testSlotDTO;
    private Enseignant testEnseignant;
    private Salle testSalle;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(calendarController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Pour supporter LocalDateTime

        // Initialisation des objets de test
        testEnseignant = new Enseignant();
        testEnseignant.setId(1L);
        testEnseignant.setNom("Dupont");
        testEnseignant.setPrenom("Jean");

        testSalle = new Salle();
        testSalle.setId(1L);
        testSalle.setNom("Salle A1");

        testSlot = new CalendarSlot();
        testSlot.setId(1);
        testSlot.setNom("Cours de Mathématiques");
        testSlot.setDescription("Cours d'algèbre avancée");
        testSlot.setTimeBegin(LocalDateTime.of(2024, 1, 15, 9, 0));
        testSlot.setTimeEnd(LocalDateTime.of(2024, 1, 15, 11, 0));
        testSlot.setVersion(1);
        testSlot.setEnseignant(testEnseignant);
        testSlot.setSalle(testSalle);

        testSlotDTO = Convertor.toCalendarSlotDTO(testSlot);
    }

    // Test 1: Récupérer tous les slots sans filtres
    @Test
    void getSlotsBetween_NoFilters_ShouldReturnAllSlots() throws Exception {
        // Arrange
        List<CalendarSlot> slots = Arrays.asList(testSlot);
        when(calendarSlotRepository.findAll()).thenReturn(slots);

        // Act & Assert
        mockMvc.perform(get("/timeslots")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nom").value("Cours de Mathématiques"));

        verify(calendarSlotRepository, times(1)).findAll();
        verify(calendarSlotRepository, never()).findByTimeBeginBetween(any(), any());
    }

    // Test 2: Récupérer les slots entre deux dates
    @Test
    void getSlotsBetween_WithDateFilters_ShouldReturnFilteredSlots() throws Exception {
        // Arrange
        List<CalendarSlot> slots = Arrays.asList(testSlot);
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        when(calendarSlotRepository.findByTimeBeginBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(slots);

        // Act & Assert
        mockMvc.perform(get("/timeslots")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(calendarSlotRepository, times(1)).findByTimeBeginBetween(any(), any());
    }

    // Test 3: Dates invalides (startDate après endDate)
    @Test
    void getSlotsBetween_InvalidDateRange_ShouldReturnBadRequest() throws Exception {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 31);
        LocalDate endDate = LocalDate.of(2024, 1, 1);

        // Act & Assert
        mockMvc.perform(get("/timeslots")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La date de début doit être avant la date de fin"));

        verify(calendarSlotRepository, never()).findByTimeBeginBetween(any(), any());
        verify(calendarSlotRepository, never()).findAll();
    }

    // Test 4: Récupérer les slots par date spécifique
    @Test
    void getSlotsByDate_ValidDate_ShouldReturnSlots() throws Exception {
        // Arrange
        List<CalendarSlot> slots = Arrays.asList(testSlot);
        LocalDate date = LocalDate.of(2024, 1, 15);

        when(calendarSlotRepository.findByDate(date)).thenReturn(slots);

        // Act & Assert
        mockMvc.perform(get("/timeslots/by-date")
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nom").value("Cours de Mathématiques"));

        verify(calendarSlotRepository, times(1)).findByDate(date);
    }

    // Test 5: Récupérer un slot par ID existant
    @Test
    void getSlotById_ExistingId_ShouldReturnSlot() throws Exception {
        // Arrange
        when(calendarSlotRepository.findById(1)).thenReturn(Optional.of(testSlot));

        // Act & Assert
        mockMvc.perform(get("/timeslots/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Cours de Mathématiques"))
                .andExpect(jsonPath("$.enseignantId").value(1))
                .andExpect(jsonPath("$.salleId").value(1));

        verify(calendarSlotRepository, times(1)).findById(1);
    }

    // Test 6: Récupérer un slot par ID non existant
    @Test
    void getSlotById_NonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(calendarSlotRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/timeslots/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(calendarSlotRepository, times(1)).findById(999);
    }

    // Test 7: Créer un slot valide
    @Test
    void createSlot_ValidSlot_ShouldCreateAndReturnId() throws Exception {
        // Arrange
        CalendarSlotDTO newSlotDTO = new CalendarSlotDTO(
                0, "Nouveau Cours", "Description du nouveau cours",
                LocalDateTime.of(2024, 1, 16, 10, 0),
                LocalDateTime.of(2024, 1, 16, 12, 0),
                1, 1L, 1L
        );

        CalendarSlot savedSlot = new CalendarSlot();
        savedSlot.setId(2);
        savedSlot.setNom(newSlotDTO.nom());
        savedSlot.setDescription(newSlotDTO.description());
        savedSlot.setTimeBegin(newSlotDTO.timeBegin());
        savedSlot.setTimeEnd(newSlotDTO.timeEnd());
        savedSlot.setVersion(newSlotDTO.version());

        when(enseignantRepository.findById(1L)).thenReturn(Optional.of(testEnseignant));
        when(salleRepository.findById(1L)).thenReturn(Optional.of(testSalle));
        when(calendarSlotRepository.save(any(CalendarSlot.class))).thenReturn(savedSlot);

        // Act & Assert
        mockMvc.perform(post("/timeslots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSlotDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("2"));

        verify(calendarSlotRepository, times(1)).save(any(CalendarSlot.class));
        verify(enseignantRepository, times(1)).findById(1L);
        verify(salleRepository, times(1)).findById(1L);
    }

    // Test 8: Créer un slot sans nom (invalide)
    @Test
    void createSlot_WithoutName_ShouldReturnBadRequest() throws Exception {
        // Arrange
        CalendarSlotDTO invalidSlotDTO = new CalendarSlotDTO(
                0, "", "Description sans nom",
                LocalDateTime.of(2024, 1, 16, 10, 0),
                LocalDateTime.of(2024, 1, 16, 12, 0),
                1, null, null
        );

        // Act & Assert
        mockMvc.perform(post("/timeslots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSlotDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Le nom est obligatoire"));

        verify(calendarSlotRepository, never()).save(any(CalendarSlot.class));
    }

    // Test 9: Créer un slot avec dates invalides
    @Test
    void createSlot_WithInvalidDates_ShouldReturnBadRequest() throws Exception {
        // Arrange
        CalendarSlotDTO invalidSlotDTO = new CalendarSlotDTO(
                0, "Cours invalide", "Dates incohérentes",
                LocalDateTime.of(2024, 1, 16, 14, 0), // Début après la fin
                LocalDateTime.of(2024, 1, 16, 12, 0),
                1, null, null
        );

        // Act & Assert
        mockMvc.perform(post("/timeslots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSlotDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La date de début doit être avant la date de fin"));

        verify(calendarSlotRepository, never()).save(any(CalendarSlot.class));
    }

    // Test 10: Créer un slot avec enseignant inexistant
    @Test
    void createSlot_WithNonExistingEnseignant_ShouldReturnBadRequest() throws Exception {
        // Arrange
        CalendarSlotDTO slotDTO = new CalendarSlotDTO(
                0, "Cours avec enseignant inexistant", "Description",
                LocalDateTime.of(2024, 1, 16, 10, 0),
                LocalDateTime.of(2024, 1, 16, 12, 0),
                1, 999L, null
        );

        when(enseignantRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/timeslots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Enseignant non trouvé avec l'ID: 999"));

        verify(calendarSlotRepository, never()).save(any(CalendarSlot.class));
    }

    // Test 11: Mettre à jour un slot existant
    @Test
    void updateSlot_ExistingSlot_ShouldUpdateAndReturnSlot() throws Exception {
        // Arrange
        CalendarSlotDTO updatedDTO = new CalendarSlotDTO(
                1, "Cours Modifié", "Description modifiée",
                LocalDateTime.of(2024, 1, 15, 10, 0),
                LocalDateTime.of(2024, 1, 15, 12, 0),
                2, 1L, 1L
        );

        when(calendarSlotRepository.findById(1)).thenReturn(Optional.of(testSlot));
        when(enseignantRepository.findById(1L)).thenReturn(Optional.of(testEnseignant));
        when(salleRepository.findById(1L)).thenReturn(Optional.of(testSalle));
        when(calendarSlotRepository.save(any(CalendarSlot.class))).thenReturn(testSlot);

        // Act & Assert
        mockMvc.perform(put("/timeslots/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Cours de Mathématiques"));

        verify(calendarSlotRepository, times(1)).findById(1);
        verify(calendarSlotRepository, times(1)).save(any(CalendarSlot.class));
    }

    // Test 12: Mettre à jour un slot non existant
    @Test
    void updateSlot_NonExistingSlot_ShouldReturnNotFound() throws Exception {
        // Arrange
        CalendarSlotDTO updatedDTO = new CalendarSlotDTO(
                999, "Cours Inexistant", "Description",
                LocalDateTime.of(2024, 1, 15, 10, 0),
                LocalDateTime.of(2024, 1, 15, 12, 0),
                1, null, null
        );

        when(calendarSlotRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/timeslots/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isNotFound());

        verify(calendarSlotRepository, times(1)).findById(999);
        verify(calendarSlotRepository, never()).save(any(CalendarSlot.class));
    }

    // Test 13: Conflit de version lors de la mise à jour
    @Test
    void updateSlot_VersionConflict_ShouldReturnConflict() throws Exception {
        // Arrange
        CalendarSlotDTO updatedDTO = new CalendarSlotDTO(
                1, "Cours Modifié", "Description modifiée",
                LocalDateTime.of(2024, 1, 15, 10, 0),
                LocalDateTime.of(2024, 1, 15, 12, 0),
                0, // Version inférieure à la version courante (1)
                null, null
        );

        when(calendarSlotRepository.findById(1)).thenReturn(Optional.of(testSlot));

        // Act & Assert
        mockMvc.perform(put("/timeslots/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflit de version. Version courante: 1"));

        verify(calendarSlotRepository, times(1)).findById(1);
        verify(calendarSlotRepository, never()).save(any(CalendarSlot.class));
    }

    // Test 14: Supprimer un slot existant
    @Test
    void deleteSlot_ExistingSlot_ShouldDeleteAndReturnSuccess() throws Exception {
        // Arrange
        when(calendarSlotRepository.existsById(1)).thenReturn(true);
        doNothing().when(calendarSlotRepository).deleteById(1);

        // Act & Assert
        mockMvc.perform(delete("/timeslots/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Slot supprimé avec succès"));

        verify(calendarSlotRepository, times(1)).existsById(1);
        verify(calendarSlotRepository, times(1)).deleteById(1);
    }

    // Test 15: Supprimer un slot non existant
    @Test
    void deleteSlot_NonExistingSlot_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(calendarSlotRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/timeslots/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(calendarSlotRepository, times(1)).existsById(999);
        verify(calendarSlotRepository, never()).deleteById(anyInt());
    }

    // Test 16: Créer un slot sans enseignant ni salle
    @Test
    void createSlot_WithoutEnseignantAndSalle_ShouldCreateSuccessfully() throws Exception {
        // Arrange
        CalendarSlotDTO slotDTO = new CalendarSlotDTO(
                0, "Cours Indépendant", "Sans enseignant ni salle",
                LocalDateTime.of(2024, 1, 16, 10, 0),
                LocalDateTime.of(2024, 1, 16, 12, 0),
                1, null, null
        );

        CalendarSlot savedSlot = new CalendarSlot();
        savedSlot.setId(3);
        savedSlot.setNom(slotDTO.nom());

        when(calendarSlotRepository.save(any(CalendarSlot.class))).thenReturn(savedSlot);

        // Act & Assert
        mockMvc.perform(post("/timeslots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("3"));

        verify(calendarSlotRepository, times(1)).save(any(CalendarSlot.class));
        verify(enseignantRepository, never()).findById(any());
        verify(salleRepository, never()).findById(any());
    }

    // Test 17: Exception interne du serveur
    @Test
    void getSlotsBetween_RepositoryThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(calendarSlotRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/timeslots")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error fetching slots")));

        verify(calendarSlotRepository, times(1)).findAll();
    }
}