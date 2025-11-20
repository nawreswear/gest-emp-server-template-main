package tn.iset.m2glnt.server.model;

import jakarta.persistence.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Table(name = "calendars")
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // On ne peut pas directement stocker Map avec JPA, il faut utiliser OneToMany
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)

    @JoinColumn(name = "calendar_id") // clé étrangère dans CalendarSlot
    private List<CalendarSlot> slots = new ArrayList<>();


    public Calendar() {}

    public void addSlot(CalendarSlot slot) {
        slots.add(slot);
    }

    public CalendarSlot getSlotById(int id) {
        return slots.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    public boolean updateSlot(int id, CalendarSlot newSlot) {
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).getId() == id) {
                slots.set(i, newSlot);
                return true;
            }
        }
        return false;
    }

    public boolean removeSlotById(int id) {
        return slots.removeIf(slot -> slot.getId() == id);
    }

    public List<CalendarSlot> getSlots() {
        return slots;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}