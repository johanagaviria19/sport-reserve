package com.sportreserve.repository;

import com.sportreserve.enums.RolParticipacion;
import com.sportreserve.model.ParticipacionReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipacionReservaRepository extends JpaRepository<ParticipacionReserva, Long> {

    Optional<ParticipacionReserva> findByReservaIdAndPersonaId(Long reservaId, Long personaId);

    boolean existsByReservaIdAndPersonaId(Long reservaId, Long personaId);

    List<ParticipacionReserva> findByReservaId(Long reservaId);

    List<ParticipacionReserva> findByReservaIdAndRol(Long reservaId, RolParticipacion rol);
}
