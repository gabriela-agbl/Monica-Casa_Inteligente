package com.monica.casa.repository;

import com.monica.casa.model.Rotina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface RotinaRepository extends JpaRepository<Rotina, Long> {

    @Query("select r from Rotina r join fetch r.dispositivo order by r.horario asc, r.nome asc")
    List<Rotina> findAllComDispositivo();

    @Query("select r from Rotina r join fetch r.dispositivo where r.id = :id")
    Optional<Rotina> findByIdComDispositivo(@Param("id") Long id);

    /** Usada pelo scheduler: rotinas ativas marcadas para o minuto informado. */
    @Query("select r from Rotina r join fetch r.dispositivo where r.ativo = true and r.horario = :horario")
    List<Rotina> findAtivasPorHorario(@Param("horario") LocalTime horario);

    long countByAtivoTrue();

    void deleteByDispositivoId(Long dispositivoId);
}
