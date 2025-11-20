package tn.iset.m2glnt.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.iset.m2glnt.server.model.Calendar;
import tn.iset.m2glnt.server.model.CalendarSlot;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

}
