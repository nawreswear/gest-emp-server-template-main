package tn.iset.m2glnt.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.iset.m2glnt.server.model.Salle;
import tn.iset.m2glnt.server.repository.SalleRepository;
import tn.iset.m2glnt.server.repository.CalendarSlotRepository;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class SalleServiceImpl implements SalleService {

    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private CalendarSlotRepository calendarSlotRepository; // Ajout du repository pour les slots

    @Override
    public Salle saveSalle(Salle s) {
        if (salleRepository != null) {
            return salleRepository.save(s);
        }
        return null;
    }

    @Override
    public List<Salle> getAllSalles() {
        return salleRepository.findAll();
    }

    @Override
    public Salle updateSalle(Salle s) {
        if (salleRepository != null) {
            Salle existingSalle = salleRepository.findById(s.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Salle not found with id: " + s.getId()));

            existingSalle.setNom(s.getNom());
            existingSalle.setCapacite(s.getCapacite());
            existingSalle.setBatiment(s.getBatiment());
            return salleRepository.save(existingSalle);
        }
        return null;
    }

    @Override
    public void deleteSalle(Long id) {
        try {
            if (salleRepository != null) {
                // Vérifier si la salle existe
                Salle salle = salleRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'ID: " + id));

                System.out.println("🗑️ Tentative de suppression de la salle ID: " + id + " - Nom: " + salle.getNom());

                // OPTION 2: Dissocier les slots (mettre à null) - RECOMMANDÉ
                if (calendarSlotRepository != null) {
                    int slotsUpdated = calendarSlotRepository.setSalleToNullBySalleId(id);
                    System.out.println("🔄 " + slotsUpdated + " slots dissociés pour la salle ID: " + id);
                }

                // Puis supprimer la salle
                salleRepository.deleteById(id);
                System.out.println("✅ Salle ID " + id + " supprimée avec succès");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur suppression salle ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    @Override
    public Salle getById(Long id) {
        Optional<Salle> salleOptional = salleRepository.findById(id);
        return salleOptional.orElse(null);
    }
}