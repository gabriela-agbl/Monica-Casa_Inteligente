package com.monica.casa.repository;

import com.monica.casa.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {

    List<Dispositivo> findAllByOrderByNomeAsc();

    /** Dispositivos que o admin liberou para convidados (perfil COMUM). */
    List<Dispositivo> findByAcessoConvidadoTrueOrderByNomeAsc();

    long countByStatusTrue();

    boolean existsByUsuarioId(Long usuarioId);
}
