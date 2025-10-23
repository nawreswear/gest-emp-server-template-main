package tn.iset.m2glnt.server.controler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;
import tn.iset.m2glnt.server.model.CalendarSlot;
import tn.iset.m2glnt.server.model.technical.Convertor;
import tn.iset.m2glnt.server.repository.CalendarSlotRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(CalendarController.class)
class CalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalendarSlotRepository repository;

    @Test
    void getSlotsBetween_ShouldReturnFilteredSlots() throws Exception {
        // Arrange
        LocalDate start = LocalDate.of(2025, 1, 8);
        LocalDate end = LocalDate.of(2025, 1, 10);
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 8, 8, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 1, 8, 9, 0);

        CalendarSlot slot = new CalendarSlot(1, startTime, endTime, "Test Slot", 1);
        when(repository.findAll()).thenReturn(List.of(slot));

        // Act & Assert
        mockMvc.perform(get("/timeslots")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test Slot"));
    }

    @Test
    void getSlotById_ShouldReturnSlot() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        CalendarSlot slot = new CalendarSlot(1, startTime, endTime, "Test", 1);
        when(repository.findById(1)).thenReturn(Optional.of(slot));

        // Act
        CalendarController controller = new CalendarController();
        // CORRECTION : Utilisez l'injection ou les setters
        controller.repository = repository;
        var response = controller.getSlotById(1);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        CalendarSlotDTO dto = response.getBody();
        assertNotNull(dto);
        assertEquals(1, dto.id());
        assertEquals("Test", dto.description());
    }

    @Test
    void getAllSlots_ShouldReturnAllSlots() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        CalendarSlot slot = new CalendarSlot(1, startTime, endTime, "Test", 1);
        when(repository.findAll()).thenReturn(List.of(slot));

        // Act
        CalendarController controller = new CalendarController();
        controller.repository = repository;
        List<CalendarSlotDTO> result = controller.getAllSlots();

        // Assert
        assertEquals(1, result.size());
        CalendarSlotDTO dto = result.get(0);
        assertEquals(1, dto.id());
        assertEquals("Test", dto.description());
    }

    // CORRECTION : Supprimez les méthodes utilitaires problématiques
    // Ces méthodes ne sont pas nécessaires pour les tests du contrôleur
}