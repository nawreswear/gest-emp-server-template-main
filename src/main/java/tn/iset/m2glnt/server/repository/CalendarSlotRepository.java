package tn.iset.m2glnt.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import tn.iset.m2glnt.server.model.CalendarSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
public interface CalendarSlotRepository extends JpaRepository<CalendarSlot, Integer> {

    @Query("SELECT cs FROM CalendarSlot cs WHERE DATE(cs.timeBegin) = :date")
    List<CalendarSlot> findByDate(@Param("date") LocalDate date);

    // CORRECTION : Supprimer la méthode en double ou la corriger
    @Query("SELECT s FROM CalendarSlot s WHERE s.timeBegin BETWEEN :start AND :end")
    List<CalendarSlot> findByTimeBeginBetween(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    // Vérifier si un enseignant a des slots
    boolean existsByEnseignantId(Long enseignantId);

    // Pour compter les slots utilisant cette salle
    @Query("SELECT COUNT(s) FROM CalendarSlot s WHERE s.salle.id = :salleId")
    long countBySalleId(@Param("salleId") Long salleId);
    @Modifying
    @Transactional // 🔥 AJOUTER CETTE ANNOTATION
    @Query("UPDATE CalendarSlot s SET s.salle = null WHERE s.salle.id = :salleId")
    int setSalleToNullBySalleId(@Param("salleId") Long salleId);
    /*@Modifying
    @Transactional // 🔥 AJOUTER CETTE ANNOTATION
    @Query("UPDATE CalendarSlot s SET s.enseignant = null WHERE s.enseignant.id = :enseignantId")
    int setEnseignantToNullByEnseignantId(@Param("enseignantId") Long enseignantId);
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM CalendarSlot s WHERE s.salle.id = :salleId")*/
    boolean existsBySalleId(@Param("salleId") Long salleId);
    // Supprimer tous les slots d'un enseignant
    @Modifying
    @Query("DELETE FROM CalendarSlot c WHERE c.enseignant.id = :enseignantId")
    int deleteByEnseignantId(@Param("enseignantId") Long enseignantId);

    // Alternative: dissocier les slots (mettre enseignant à null)
   /* @Modifying
    @Query("UPDATE CalendarSlot c SET c.enseignant = null WHERE c.enseignant.id = :enseignantId")
    int setEnseignantToNullByEnseignantId(@Param("enseignantId") Long enseignantId);*/
}