package tn.iset.m2glnt.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.iset.m2glnt.server.model.Enseignant;
import tn.iset.m2glnt.server.repository.CalendarSlotRepository;
import tn.iset.m2glnt.server.repository.EnseignantRepository;

import java.util.List;

@Transactional
@Service
public class EnseignantServiceImpl implements EnseignantService {

    @Autowired
    private EnseignantRepository enseignantRepo;

    @Autowired
    private CalendarSlotRepository calendarSlotRepo; // Ajoutez cette dépendance

    @Override
    public Enseignant save(Enseignant e) {
        if (enseignantRepo != null) {
            return enseignantRepo.save(e);
        }
        return null;
    }

    @Override
    public List<Enseignant> getAll() {
        return enseignantRepo.findAll();
    }

    @Override
    public Enseignant getById(Long id) {
        return enseignantRepo.findById(id).orElse(null);
    }

    @Override
    public Enseignant updateEnseignant(Enseignant updatedEnseignant) {
        if (enseignantRepo != null) {
            Enseignant existing = enseignantRepo.findById(updatedEnseignant.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Enseignant not found with id: " + updatedEnseignant.getId()));

            existing.setNom(updatedEnseignant.getNom());
            existing.setPrenom(updatedEnseignant.getPrenom());
            existing.setEmail(updatedEnseignant.getEmail());
            existing.setPassword(updatedEnseignant.getPassword());
            existing.setTel(updatedEnseignant.getTel());
            existing.setCin(updatedEnseignant.getCin());
            existing.setType(updatedEnseignant.getType());
            existing.setPhoto(updatedEnseignant.getPhoto());

            return enseignantRepo.save(existing);
        }
        return null;
    }

    @Override
    public void deleteEnseignant(Long id) {
        try {
            if (enseignantRepo != null) {
                // Vérifier si l'enseignant existe
                Enseignant enseignant = enseignantRepo.findById(id)
                        .orElseThrow(() -> new RuntimeException("Enseignant non trouvé avec l'ID: " + id));

                // OPTION 1: Supprimer les slots associés
                if (calendarSlotRepo != null) {
                    int slotsDeleted = calendarSlotRepo.deleteByEnseignantId(id);
                    System.out.println("🗑️ " + slotsDeleted + " slots supprimés pour l'enseignant ID: " + id);
                }

                // OPTION 2: Ou dissocier les slots (mettre à null)
                // calendarSlotRepo.setEnseignantToNullByEnseignantId(id);

                // Puis supprimer l'enseignant
                enseignantRepo.deleteById(id);
                System.out.println("✅ Enseignant ID " + id + " supprimé avec succès");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur suppression enseignant ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage());
        }
    }
}