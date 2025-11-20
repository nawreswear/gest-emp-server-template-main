package tn.iset.m2glnt.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.iset.m2glnt.server.model.Calendar;
import tn.iset.m2glnt.server.model.Enseignant;

public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {}

