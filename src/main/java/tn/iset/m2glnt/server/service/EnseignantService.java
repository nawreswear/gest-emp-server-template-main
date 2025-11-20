package tn.iset.m2glnt.server.service;



import tn.iset.m2glnt.server.model.Enseignant;

import java.util.List;

public interface EnseignantService {
    Enseignant save(Enseignant e);        // <-- Added save method
    List<Enseignant> getAll();                   // Read all
    Enseignant getById(Long id);                 // Read one
    Enseignant updateEnseignant(Enseignant e);   // Update
    void deleteEnseignant(Long id);

}
