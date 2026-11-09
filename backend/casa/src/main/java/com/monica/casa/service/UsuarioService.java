package com.monica.casa.service;

import com.monica.casa.dto.UsuarioDTO;
import com.monica.casa.exception.ConflitoException;
import com.monica.casa.exception.RecursoNaoEncontradoException;
import com.monica.casa.exception.RegraNegocioException;
import com.monica.casa.model.Perfil;
import com.monica.casa.model.Usuario;
import com.monica.casa.repository.AlertaRepository;
import com.monica.casa.repository.ConfiguracaoRepository;
import com.monica.casa.repository.DispositivoRepository;
import com.monica.casa.repository.UsuarioRepository;
import com.monica.casa.util.TelefoneUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final DispositivoRepository dispositivoRepository;
    private final AlertaRepository alertaRepository;
    private final ConfiguracaoRepository configuracaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${monica.registro-publico:true}")
    private boolean registroPublico;

    /** Auto-cadastro: sempre perfil COMUM (ninguém vira ADMIN sozinho). */
    @Transactional
    public UsuarioDTO.Resposta registrar(UsuarioDTO.Registro req) {
        if (!registroPublico) {
            throw new AccessDeniedException("Cadastro público desabilitado.");
        }
        return criar(req.nome(), req.email(), req.senha(), req.telefone(), Perfil.COMUM);
    }

    @Transactional
    public UsuarioDTO.Resposta criarPorAdmin(UsuarioDTO.CriacaoAdmin req) {
        return criar(req.nome(), req.email(), req.senha(), req.telefone(), req.perfil());
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO.Resposta> listar() {
        return usuarioRepository.findAllByOrderByNomeAsc().stream().map(UsuarioDTO.Resposta::de).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO.Resposta buscar(Long id) {
        return UsuarioDTO.Resposta.de(obter(id));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO.Resposta buscarPorEmail(String email) {
        return UsuarioDTO.Resposta.de(obterPorEmail(email));
    }

    @Transactional(readOnly = true)
    public Usuario obterPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + email));
    }

    @Transactional
    public UsuarioDTO.Resposta atualizar(Long id, UsuarioDTO.Atualizacao req) {
        Usuario u = obter(id);
        String telefone = telefoneValido(req.telefone());
        if (usuarioRepository.existsByTelefoneAndIdNot(telefone, id)) {
            throw new ConflitoException("Telefone já cadastrado para outro usuário.");
        }
        if (u.getPerfil() == Perfil.ADMIN && req.perfil() != Perfil.ADMIN) {
            garantirOutroAdmin();
        }
        u.setNome(req.nome().trim());
        u.setTelefone(telefone);
        u.setPerfil(req.perfil());
        if (req.senha() != null && !req.senha().isBlank()) {
            u.setSenha(passwordEncoder.encode(req.senha()));
        }
        return UsuarioDTO.Resposta.de(usuarioRepository.save(u));
    }

    @Transactional
    public void remover(Long id, String emailLogado) {
        Usuario u = obter(id);
        if (u.getEmail().equalsIgnoreCase(emailLogado)) {
            throw new RegraNegocioException("Você não pode remover a própria conta.");
        }
        if (u.getPerfil() == Perfil.ADMIN) {
            garantirOutroAdmin();
        }
        if (dispositivoRepository.existsByUsuarioId(id)) {
            throw new RegraNegocioException("Este usuário cadastrou dispositivos. Remova-os antes de excluir a conta.");
        }
        alertaRepository.deleteByUsuarioId(id);
        configuracaoRepository.deleteByUsuarioId(id);
        usuarioRepository.delete(u);
    }

    // ---------- internos ----------

    private UsuarioDTO.Resposta criar(String nome, String email, String senha, String telefone, Perfil perfil) {
        String emailNorm = email.trim().toLowerCase();
        String tel = telefoneValido(telefone);
        if (usuarioRepository.existsByEmail(emailNorm)) {
            throw new ConflitoException("E-mail já cadastrado.");
        }
        if (usuarioRepository.existsByTelefone(tel)) {
            throw new ConflitoException("Telefone já cadastrado.");
        }
        Usuario u = new Usuario();
        u.setNome(nome.trim());
        u.setEmail(emailNorm);
        u.setSenha(passwordEncoder.encode(senha)); // RNF02: BCrypt
        u.setTelefone(tel);
        u.setPerfil(perfil);
        return UsuarioDTO.Resposta.de(usuarioRepository.save(u));
    }

    private String telefoneValido(String telefone) {
        String normalizado = TelefoneUtil.normalizar(telefone);
        if (!TelefoneUtil.valido(normalizado)) {
            throw new RegraNegocioException("Telefone inválido. Informe DDD + número, ex.: (11) 99999-9999.");
        }
        return normalizado;
    }

    private void garantirOutroAdmin() {
        if (usuarioRepository.countByPerfil(Perfil.ADMIN) <= 1) {
            throw new RegraNegocioException("A casa precisa ter ao menos um administrador.");
        }
    }

    private Usuario obter(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + id));
    }
}
