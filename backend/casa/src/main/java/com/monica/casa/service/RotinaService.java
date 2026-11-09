package com.monica.casa.service;

import com.monica.casa.dto.DispositivoDTO;
import com.monica.casa.dto.RotinaDTO;
import com.monica.casa.exception.RecursoNaoEncontradoException;
import com.monica.casa.model.Dispositivo;
import com.monica.casa.model.NivelAlerta;
import com.monica.casa.model.OrigemAcionamento;
import com.monica.casa.model.Rotina;
import com.monica.casa.repository.DispositivoRepository;
import com.monica.casa.repository.RotinaRepository;
import com.monica.casa.util.DiasSemana;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RotinaService {

    private final RotinaRepository rotinaRepository;
    private final DispositivoRepository dispositivoRepository;
    private final DispositivoService dispositivoService;
    private final AlertaService alertaService;
    private final ConfiguracaoService configuracaoService;

    @Transactional(readOnly = true)
    public List<RotinaDTO.Resposta> listar() {
        return rotinaRepository.findAllComDispositivo().stream().map(RotinaDTO.Resposta::de).toList();
    }

    @Transactional(readOnly = true)
    public RotinaDTO.Resposta buscar(Long id) {
        return RotinaDTO.Resposta.de(obter(id));
    }

    @Transactional
    public RotinaDTO.Resposta criar(RotinaDTO.Requisicao req) {
        Rotina r = new Rotina();
        preencher(r, req);
        r.setAtivo(req.ativo() == null || req.ativo());
        return RotinaDTO.Resposta.de(rotinaRepository.save(r));
    }

    @Transactional
    public RotinaDTO.Resposta atualizar(Long id, RotinaDTO.Requisicao req) {
        Rotina r = obter(id);
        preencher(r, req);
        if (req.ativo() != null) {
            r.setAtivo(req.ativo());
        }
        return RotinaDTO.Resposta.de(rotinaRepository.save(r));
    }

    @Transactional
    public RotinaDTO.Resposta alterarAtivo(Long id, boolean ativo) {
        Rotina r = obter(id);
        r.setAtivo(ativo);
        return RotinaDTO.Resposta.de(rotinaRepository.save(r));
    }

    @Transactional
    public void remover(Long id) {
        rotinaRepository.delete(obter(id));
    }

    /**
     * Executa a ação da rotina sobre o dispositivo, registra no histórico e (RF05) avisa os
     * administradores pelo WhatsApp. Chamada pelo scheduler e pelo endpoint "executar agora".
     */
    @Transactional
    public DispositivoDTO.Resposta executar(Long id) {
        Rotina r = obter(id);
        Dispositivo d = r.getDispositivo();
        boolean ligar = r.getAcao().isLigado();

        boolean mudou = dispositivoService.aplicarStatus(d, ligar, OrigemAcionamento.ROTINA, "Rotina: " + r.getNome());

        if (configuracaoService.getBoolean(ConfiguracaoService.NOTIFICAR_ROTINAS, true)) {
            String estado = ligar ? "ligado" : "desligado";
            String detalhe = mudou ? estado : "já estava " + estado;
            alertaService.notificarAdmins("Rotina executada",
                    "\"" + r.getNome() + "\" (" + r.getHorario() + "): " + d.getNome()
                            + (d.getComodo() != null ? " - " + d.getComodo() : "") + " " + detalhe + ".",
                    NivelAlerta.INFO);
        }
        return DispositivoDTO.Resposta.de(d);
    }

    // ---------- internos ----------

    private void preencher(Rotina r, RotinaDTO.Requisicao req) {
        Dispositivo d = dispositivoRepository.findById(req.dispositivoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dispositivo não encontrado: " + req.dispositivoId()));
        r.setNome(req.nome().trim());
        r.setHorario(req.horario().truncatedTo(ChronoUnit.MINUTES)); // o scheduler compara minuto a minuto
        r.setDiasSemana(DiasSemana.normalizar(req.diasSemana()));
        r.setAcao(req.acao());
        r.setDispositivo(d);
    }

    private Rotina obter(Long id) {
        return rotinaRepository.findByIdComDispositivo(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Rotina não encontrada: " + id));
    }
}
