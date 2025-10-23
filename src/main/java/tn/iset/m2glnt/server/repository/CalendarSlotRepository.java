package tn.iset.m2glnt.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.iset.m2glnt.server.model.CalendarSlot;

import java.time.LocalDate;
import java.util.List;

public interface CalendarSlotRepository extends JpaRepository<CalendarSlot, Integer> {
    @Query("SELECT s FROM CalendarSlot s WHERE DATE(s.time_begin) = :date")
    List<CalendarSlot> findByDate(LocalDate date);
}
