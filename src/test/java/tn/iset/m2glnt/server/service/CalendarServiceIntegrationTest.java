package tn.iset.m2glnt.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CalendarServiceIntegrationTest {

    @Autowired
    private CalendarService calendarService;

    @Test
    void testGetAllSlots_Integration() {
        // Act
        List<CalendarSlotDTO> result = calendarService.getAllSlots();

        // Assert
        assertNotNull(result);
        // Les assertions dépendent de l'état de votre base de données
    }

    // Autres tests d'intégration...
}