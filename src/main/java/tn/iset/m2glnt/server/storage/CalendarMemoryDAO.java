package tn.iset.m2glnt.server.storage;

import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.model.DAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CalendarMemoryDAO implements DAO<Integer, CalendarSlotDTO> {

    private final List<CalendarSlotDTO> slots = new ArrayList<>();

    // ✅ Ajout du constructeur qui accepte un booléen
    public CalendarMemoryDAO(boolean preload) {
        if (preload) {
            // Tu peux initialiser des slots par défaut ici
            System.out.println("Preloading des données en mémoire...");
        }
    }

    public CalendarMemoryDAO() {
        // constructeur vide par défaut
    }

    @Override
    public Optional<CalendarSlotDTO> get(Integer key) {
        return slots.stream().filter(s -> s.id() == key).findFirst();
    }

    @Override
    public List<CalendarSlotDTO> getAll() {
        return new ArrayList<>(slots);
    }

    @Override
    public Integer create(CalendarSlotDTO element) {
        slots.add(element);
        return element.id();
    }

    @Override
    public int update(CalendarSlotDTO element) {
        delete(element);
        slots.add(element);
        return element.version();
    }

    @Override
    public void delete(CalendarSlotDTO element) {
        slots.removeIf(s -> s.id() == element.id());
    }
}
