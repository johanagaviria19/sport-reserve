package com.sportreserve.repository;

import com.sportreserve.enums.EstadoReserva;
import com.sportreserve.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByEscenarioIdAndFecha(Long escenarioId, LocalDate fecha);

    @Query("SELECT r FROM Reserva r " +
           "WHERE r.escenario.id = :escenarioId " +
           "AND r.fecha = :fecha " +
           "AND r.estado <> :estadoExcluido " +
           "AND r.horaInicio < :horaFinNueva " +
           "AND r.horaFin > :horaInicioNueva")
    List<Reserva> findReservasConflictoHorario(
            @Param("escenarioId") Long escenarioId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicioNueva") LocalTime horaInicioNueva,
            @Param("horaFinNueva") LocalTime horaFinNueva,
            @Param("estadoExcluido") EstadoReserva estadoExcluido);
}
