package com.monica.casa.repository;

<<<<<<< HEAD
import com.monica.casa.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
=======
import com.monica.casa.model.Perfil;
import com.monica.casa.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByTelefone(String telefone);

    boolean existsByTelefoneAndIdNot(String telefone, Long id);

    List<Usuario> findByPerfil(Perfil perfil);

    long countByPerfil(Perfil perfil);

    List<Usuario> findAllByOrderByNomeAsc();
}
>>>>>>> 8adff7e (Implementa backend Spring Boot da Monica Casa Inteligente)
