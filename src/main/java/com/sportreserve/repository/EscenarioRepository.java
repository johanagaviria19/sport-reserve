package com.sportreserve.repository;

import com.sportreserve.model.Escenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscenarioRepository extends JpaRepository<Escenario, Long> {
}
