package com.monica.casa.repository;

import com.monica.casa.model.HistoricoDispositivo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoricoDispositivoRepository extends JpaRepository<HistoricoDispositivo, Long> {

    @Query("select h from HistoricoDispositivo h join fetch h.dispositivo "
            + "where h.dispositivo.id = :dispositivoId order by h.criadoEm desc")
    List<HistoricoDispositivo> findByDispositivo(@Param("dispositivoId") Long dispositivoId, Pageable pageable);

    @Query("select h from HistoricoDispositivo h join fetch h.dispositivo order by h.criadoEm desc")
    List<HistoricoDispositivo> findRecentes(Pageable pageable);

    void deleteByDispositivoId(Long dispositivoId);
}
