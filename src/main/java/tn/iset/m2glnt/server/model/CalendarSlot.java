package tn.iset.m2glnt.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tn.iset.m2glnt.server.model.Enseignant;
import tn.iset.m2glnt.server.model.Salle;

import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_slots")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CalendarSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 50)
    private String nom;

    // CORRECTION : Supprimer l'annotation @Column ou l'ajuster
    private LocalDateTime timeBegin;

    // CORRECTION : Ajouter @Column pour time_end si nécessaire
    @Column(name = "time_end")
    private LocalDateTime timeEnd; // Renommer en camelCase pour la cohérence

    private String description;
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enseignant_id")
    @ToString.Exclude
    private Enseignant enseignant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id")
    @ToString.Exclude
    private Salle salle;

    // Constructeur mis à jour
    public CalendarSlot(Integer id, String nom, LocalDateTime timeBegin, LocalDateTime timeEnd,
                        String description, int version, Enseignant enseignant, Salle salle) {
        this.id = id;
        this.nom = nom;
        this.timeBegin = timeBegin;
        this.timeEnd = timeEnd;
        this.description = description;
        this.version = version;
        this.enseignant = enseignant;
        this.salle = salle;
    }
}