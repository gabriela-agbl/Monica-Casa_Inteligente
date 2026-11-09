package com.monica.casa.dto;

import com.monica.casa.model.Dispositivo;
import com.monica.casa.model.HistoricoDispositivo;
import com.monica.casa.model.OrigemAcionamento;
import com.monica.casa.model.TipoDispositivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class DispositivoDTO {

    private DispositivoDTO() {
    }

    public record Requisicao(
            @NotBlank(message = "Nome é obrigatório") @Size(max = 100, message = "Nome deve ter até 100 caracteres") String nome,
            @NotNull(message = "Tipo é obrigatório (ILUMINACAO, CLIMATIZACAO, IRRIGACAO, SEGURANCA, OUTRO)") TipoDispositivo tipo,
            @Size(max = 50, message = "Cômodo deve ter até 50 caracteres") String comodo,
            Boolean acessoConvidado) {
    }

    public record AlterarStatus(
            @NotNull(message = "Informe 'ligado': true ou false") Boolean ligado) {
    }

    public record Resposta(Long id, String nome, TipoDispositivo tipo, Boolean status, String comodo,
                           Boolean acessoConvidado, Long proprietarioId) {
        public static Resposta de(Dispositivo d) {
            return new Resposta(d.getId(), d.getNome(), d.getTipo(), d.getStatus(), d.getComodo(),
                    d.getAcessoConvidado(), d.getUsuario().getId());
        }
    }

    public record Historico(Long id, Long dispositivoId, String dispositivoNome, Boolean ligado,
                            OrigemAcionamento origem, String acionadoPor, LocalDateTime criadoEm) {
        public static Historico de(HistoricoDispositivo h) {
            return new Historico(h.getId(), h.getDispositivo().getId(), h.getDispositivo().getNome(),
                    h.getLigado(), h.getOrigem(), h.getAcionadoPor(), h.getCriadoEm());
        }
    }
}
