package tn.iset.m2glnt.server.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour CalendarMemoryDAO
 */
class CalendarMemoryDAOTest {

    private CalendarMemoryDAO dao;
    private CalendarSlotDTO slot1;
    private CalendarSlotDTO slot2;

    @BeforeEach
    void setUp() {
        dao = new CalendarMemoryDAO(true);

        // Création de deux slots fictifs avec le bon ordre des paramètres
        slot1 = new CalendarSlotDTO(
                1,
                "Cours de Java",
                new TimeInterval(
                        LocalDateTime.of(2025, 10, 23, 8, 0),
                        LocalDateTime.of(2025, 10, 23, 10, 0)
                ),
                1
        );

        slot2 = new CalendarSlotDTO(
                2,
                "TP de Spring Boot",
                new TimeInterval(
                        LocalDateTime.of(2025, 10, 24, 14, 0),
                        LocalDateTime.of(2025, 10, 24, 16, 0)
                ),
                1
        );
    }

    @Test
    void testCreateAndGet() {
        dao.create(slot1);
        dao.create(slot2);

        Optional<CalendarSlotDTO> found = dao.get(1);
        assertTrue(found.isPresent(), "Le slot doit exister");
        assertEquals("Cours de Java", found.get().description());
    }

    @Test
    void testGetAll() {
        dao.create(slot1);
        dao.create(slot2);

        List<CalendarSlotDTO> all = dao.getAll();
        assertEquals(2, all.size(), "Il doit y avoir deux slots");
    }

    @Test
    void testUpdate() {
        dao.create(slot1);

        CalendarSlotDTO updatedSlot = new CalendarSlotDTO(
                1,
                "Cours de Java - modifié",
                new TimeInterval(
                        LocalDateTime.of(2025, 10, 23, 9, 0),
                        LocalDateTime.of(2025, 10, 23, 11, 0)
                ),
                2
        );

        dao.update(updatedSlot);
        Optional<CalendarSlotDTO> found = dao.get(1);

        assertTrue(found.isPresent(), "Le slot doit être trouvé");
        assertEquals("Cours de Java - modifié", found.get().description());
        assertEquals(2, found.get().version());
    }

    @Test
    void testDelete() {
        dao.create(slot1);
        dao.create(slot2);

        dao.delete(slot1);

        List<CalendarSlotDTO> all = dao.getAll();
        assertEquals(1, all.size(), "Un seul slot doit rester après suppression");
        assertEquals(2, all.get(0).id());
    }

    @Test
    void testConstructorWithPreload() {
        CalendarMemoryDAO dao2 = new CalendarMemoryDAO(true);
        assertNotNull(dao2.getAll(), "La liste doit être initialisée même avec preload");
    }
}
