package tn.iset.m2glnt.server.service;

import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;
import tn.iset.m2glnt.server.model.CalendarSlot;
import tn.iset.m2glnt.server.model.DAO;
import tn.iset.m2glnt.server.model.technical.Convertor;
import tn.iset.m2glnt.server.repository.CalendarSlotRepository;
import tn.iset.m2glnt.server.storage.CalendarMemoryDAO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CalendarServiceTest {

    @Mock
    private CalendarSlotRepository calendarRepository;

    @Mock
    private CalendarSlotRepository slotRepository;

    @Mock
    private DAO<Integer, CalendarSlotDTO> dao;

    @InjectMocks
    private CalendarService calendarService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialisation manuelle car le constructeur fait appel à loadAll()
        calendarService = new CalendarService();
        // Injection manuelle des mocks
        calendarService.calendarRepository = calendarRepository;
        calendarService.slotRepository = slotRepository;
        // Pour le DAO, vous devrez peut-être créer un setter ou utiliser la réflexion
    }

    @Test
    void testGetAllSlots() {
        // Arrange
        CalendarSlot slot = new CalendarSlot(1,
                LocalDateTime.of(2025, 1, 8, 8, 0),
                LocalDateTime.of(2025, 1, 8, 9, 0),
                "Test Slot", 1);
        when(slotRepository.findAll()).thenReturn(List.of(slot));

        // Act
        List<CalendarSlotDTO> result = calendarService.getAllSlots();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(slotRepository, times(1)).findAll();
    }

    @Test
    void testGetSlotsBetween() {
        // Arrange
        java.time.LocalDate start = java.time.LocalDate.of(2025, 1, 8);
        java.time.LocalDate end = java.time.LocalDate.of(2025, 1, 8);

        CalendarSlot slot = new CalendarSlot(1,
                LocalDateTime.of(2025, 1, 8, 8, 0),
                LocalDateTime.of(2025, 1, 8, 9, 0),
                "Test Slot", 1);
        when(slotRepository.findAll()).thenReturn(List.of(slot));

        // Act
        List<CalendarSlotDTO> result = calendarService.getSlotsBetween(start, end);

        // Assert
        assertNotNull(result);
        verify(slotRepository, times(1)).findAll();
    }

    @Test
    void testGetSlotById_Found() {
        // Arrange
        int testId = 1;
        CalendarSlot slot = new CalendarSlot(testId,
                LocalDateTime.of(2025, 1, 8, 8, 0),
                LocalDateTime.of(2025, 1, 8, 9, 0),
                "Test Slot", 1);
        when(slotRepository.findById(testId)).thenReturn(Optional.of(slot));

        // Act
        Optional<CalendarSlotDTO> result = calendarService.getSlotById(testId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testId, result.get().id());
        verify(slotRepository, times(1)).findById(testId);
    }

    @Test
    void testGetSlotById_NotFound() {
        // Arrange
        int testId = 999;
        when(slotRepository.findById(testId)).thenReturn(Optional.empty());

        // Act
        Optional<CalendarSlotDTO> result = calendarService.getSlotById(testId);

        // Assert
        assertFalse(result.isPresent());
        verify(slotRepository, times(1)).findById(testId);
    }

    @Test
    void testCreateSlot() {
        // Arrange
        CalendarSlotDTO newSlot = new CalendarSlotDTO(
                0,
                "Test Slot",
                new TimeInterval(
                        LocalDateTime.of(2025, 1, 8, 10, 0),
                        LocalDateTime.of(2025, 1, 8, 11, 0)
                ),
                0
        );

        CalendarSlot savedSlot = new CalendarSlot(12,
                newSlot.timeInterval().start(),
                newSlot.timeInterval().end(),
                newSlot.description(),
                newSlot.version());

        when(slotRepository.save(any(CalendarSlot.class))).thenReturn(savedSlot);

        // Act
        Integer createdId = calendarService.createSlot(1, newSlot);

        // Assert
        assertNotNull(createdId);
        assertEquals(12, createdId); // L'ID généré par le repository mocké
        verify(slotRepository, times(1)).save(any(CalendarSlot.class));
    }

    @Test
    void testUpdateSlot_Success() {
        // Arrange
        int testId = 1;
        CalendarSlotDTO updatedSlot = new CalendarSlotDTO(
                testId,
                "Updated Slot",
                new TimeInterval(
                        LocalDateTime.of(2025, 1, 8, 10, 0),
                        LocalDateTime.of(2025, 1, 8, 11, 0)
                ),
                1
        );

        CalendarSlot existingSlot = new CalendarSlot(testId,
                LocalDateTime.of(2025, 1, 8, 8, 0),
                LocalDateTime.of(2025, 1, 8, 9, 0),
                "Original Slot", 0);

        when(slotRepository.findById(testId)).thenReturn(Optional.of(existingSlot));
        when(slotRepository.save(any(CalendarSlot.class))).thenReturn(existingSlot);

        // Act
        boolean result = calendarService.updateSlot(testId, updatedSlot);

        // Assert
        assertTrue(result);
        verify(slotRepository, times(1)).findById(testId);
        verify(slotRepository, times(1)).save(existingSlot);
    }

    @Test
    void testUpdateSlot_NotFound() {
        // Arrange
        int testId = 999;
        CalendarSlotDTO updatedSlot = new CalendarSlotDTO(
                testId,
                "Updated Slot",
                new TimeInterval(
                        LocalDateTime.of(2025, 1, 8, 10, 0),
                        LocalDateTime.of(2025, 1, 8, 11, 0)
                ),
                1
        );

        when(slotRepository.findById(testId)).thenReturn(Optional.empty());

        // Act
        boolean result = calendarService.updateSlot(testId, updatedSlot);

        // Assert
        assertFalse(result);
        verify(slotRepository, times(1)).findById(testId);
        verify(slotRepository, never()).save(any(CalendarSlot.class));
    }

    @Test
    void testDeleteSlot_Success() {
        // Arrange
        int testId = 1;
        CalendarSlot existingSlot = new CalendarSlot(testId,
                LocalDateTime.of(2025, 1, 8, 8, 0),
                LocalDateTime.of(2025, 1, 8, 9, 0),
                "Test Slot", 1);

        when(slotRepository.findById(testId)).thenReturn(Optional.of(existingSlot));
        doNothing().when(slotRepository).deleteById(testId);

        // Act
        boolean result = calendarService.deleteSlot(testId);

        // Assert
        assertTrue(result);
        verify(slotRepository, times(1)).findById(testId);
        verify(slotRepository, times(1)).deleteById(testId);
    }

    @Test
    void testDeleteSlot_NotFound() {
        // Arrange
        int testId = 999;
        when(slotRepository.findById(testId)).thenReturn(Optional.empty());

        // Act
        boolean result = calendarService.deleteSlot(testId);

        // Assert
        assertFalse(result);
        verify(slotRepository, times(1)).findById(testId);
        verify(slotRepository, never()).deleteById(anyInt());
    }

    @Test
    void testGetCalendarSlotsIn() {
        // Arrange
        TimeInterval interval = new TimeInterval(
                LocalDateTime.of(2025, 1, 8, 7, 0),
                LocalDateTime.of(2025, 1, 8, 10, 0)
        );

        // Act
        var result = calendarService.getCalendarSlotsIn(interval);

        // Assert
        assertNotNull(result);
        assertNotNull(result.calendarSlots());
        // Cette méthode utilise le calendrier en mémoire, pas le repository
    }
}