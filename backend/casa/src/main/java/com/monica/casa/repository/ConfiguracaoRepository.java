package com.monica.casa.repository;

import com.monica.casa.model.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Long> {

    Optional<Configuracao> findByChave(String chave);

    List<Configuracao> findAllByOrderByChaveAsc();

    void deleteByUsuarioId(Long usuarioId);
}
