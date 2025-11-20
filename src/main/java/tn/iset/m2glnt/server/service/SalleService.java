package tn.iset.m2glnt.server.service;


import tn.iset.m2glnt.server.model.Salle;

import java.util.List;

public interface SalleService {

    Salle saveSalle(Salle s);

    List<Salle> getAllSalles();

    Salle updateSalle(Salle s);

    void deleteSalle(Long id);

    Salle getById(Long id);
}
