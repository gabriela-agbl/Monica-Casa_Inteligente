package com.monica.casa.repository;

import com.monica.casa.model.Alerta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    @Query("select a from Alerta a join fetch a.usuario order by a.criadoEm desc")
    List<Alerta> findRecentes(Pageable pageable);

    long countByCriadoEmAfter(LocalDateTime instante);

    void deleteByUsuarioId(Long usuarioId);
}
