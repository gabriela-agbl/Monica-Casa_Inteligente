package com.monica.casa.service;

import com.monica.casa.dto.DispositivoDTO;
import com.monica.casa.exception.RecursoNaoEncontradoException;
import com.monica.casa.model.Dispositivo;
import com.monica.casa.model.HistoricoDispositivo;
import com.monica.casa.model.NivelAlerta;
import com.monica.casa.model.OrigemAcionamento;
import com.monica.casa.model.TipoDispositivo;
import com.monica.casa.model.Usuario;
import com.monica.casa.repository.DispositivoRepository;
import com.monica.casa.repository.HistoricoDispositivoRepository;
import com.monica.casa.repository.RotinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DispositivoService {

    private final DispositivoRepository dispositivoRepository;
    private final HistoricoDispositivoRepository historicoRepository;
    private final RotinaRepository rotinaRepository;
    private final UsuarioService usuarioService;
    private final AlertaService alertaService;

    /** ADMIN vê tudo; COMUM vê só os dispositivos liberados para convidados. */
    @Transactional(readOnly = true)
    public List<DispositivoDTO.Resposta> listar(boolean admin) {
        List<Dispositivo> lista = admin
                ? dispositivoRepository.findAllByOrderByNomeAsc()
                : dispositivoRepository.findByAcessoConvidadoTrueOrderByNomeAsc();
        return lista.stream().map(DispositivoDTO.Resposta::de).toList();
    }

    @Transactional(readOnly = true)
    public DispositivoDTO.Resposta buscar(Long id, boolean admin) {
        Dispositivo d = obter(id);
        exigirAcesso(d, admin);
        return DispositivoDTO.Resposta.de(d);
    }

    @Transactional
    public DispositivoDTO.Resposta criar(DispositivoDTO.Requisicao req, String emailAdmin) {
        Usuario dono = usuarioService.obterPorEmail(emailAdmin);
        Dispositivo d = new Dispositivo();
        d.setUsuario(dono);
        d.setStatus(false);
        preencher(d, req);
        return DispositivoDTO.Resposta.de(dispositivoRepository.save(d));
    }

    @Transactional
    public DispositivoDTO.Resposta atualizar(Long id, DispositivoDTO.Requisicao req) {
        Dispositivo d = obter(id);
        preencher(d, req);
        return DispositivoDTO.Resposta.de(dispositivoRepository.save(d));
    }

    @Transactional
    public void remover(Long id) {
        Dispositivo d = obter(id);
        // Não depende só do ON DELETE CASCADE do init.sql (o schema pode ter sido gerado pelo Hibernate).
        rotinaRepository.deleteByDispositivoId(id);
        historicoRepository.deleteByDispositivoId(id);
        dispositivoRepository.delete(d);
    }

    /** Acionamento manual (liga/desliga) feito por um morador ou convidado autorizado. */
    @Transactional
    public DispositivoDTO.Resposta alterarStatus(Long id, boolean ligado, String emailLogado, boolean admin) {
        Dispositivo d = obter(id);
        exigirAcesso(d, admin);
        Usuario autor = usuarioService.obterPorEmail(emailLogado);
        aplicarStatus(d, ligado, OrigemAcionamento.MANUAL, autor.getNome());
        return DispositivoDTO.Resposta.de(d);
    }

    /**
     * Aplica o estado ao dispositivo e registra no histórico. Usado também pelas rotinas agendadas.
     *
     * @return true se o estado realmente mudou (se já estava no estado pedido, nada é gravado)
     */
    @Transactional
    public boolean aplicarStatus(Dispositivo d, boolean ligado, OrigemAcionamento origem, String autor) {
        if (Boolean.valueOf(ligado).equals(d.getStatus())) {
            return false;
        }
        d.setStatus(ligado);
        dispositivoRepository.save(d);

        HistoricoDispositivo h = new HistoricoDispositivo();
        h.setDispositivo(d);
        h.setLigado(ligado);
        h.setOrigem(origem);
        h.setAcionadoPor(autor);
        historicoRepository.save(h);

        if (d.getTipo() == TipoDispositivo.SEGURANCA && !ligado) {
            alertaService.notificarAdmins("Segurança desativada",
                    "O dispositivo de segurança \"" + d.getNome() + "\" foi desligado por " + autor + ".",
                    NivelAlerta.ALERTA);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public List<DispositivoDTO.Historico> historico(Long dispositivoId, int limite) {
        obter(dispositivoId); // garante 404 se o dispositivo não existir
        return historicoRepository.findByDispositivo(dispositivoId, PageRequest.of(0, limite)).stream()
                .map(DispositivoDTO.Historico::de).toList();
    }

    @Transactional(readOnly = true)
    public List<DispositivoDTO.Historico> historicoGeral(int limite) {
        return historicoRepository.findRecentes(PageRequest.of(0, limite)).stream()
                .map(DispositivoDTO.Historico::de).toList();
    }

    // ---------- internos ----------

    private void preencher(Dispositivo d, DispositivoDTO.Requisicao req) {
        d.setNome(req.nome().trim());
        d.setTipo(req.tipo());
        d.setComodo(req.comodo() == null || req.comodo().isBlank() ? null : req.comodo().trim());
        d.setAcessoConvidado(Boolean.TRUE.equals(req.acessoConvidado()));
    }

    private void exigirAcesso(Dispositivo d, boolean admin) {
        if (!admin && !Boolean.TRUE.equals(d.getAcessoConvidado())) {
            throw new AccessDeniedException("Dispositivo não liberado para convidados.");
        }
    }

    private Dispositivo obter(Long id) {
        return dispositivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dispositivo não encontrado: " + id));
    }
}
