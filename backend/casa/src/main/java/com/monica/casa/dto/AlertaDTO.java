package com.monica.casa.dto;

import com.monica.casa.model.Alerta;
import com.monica.casa.model.NivelAlerta;

import java.time.LocalDateTime;

public final class AlertaDTO {

    private AlertaDTO() {
    }

    public record Resposta(Long id, String titulo, String mensagem, NivelAlerta nivel,
                           Boolean enviadoWhatsapp, LocalDateTime criadoEm, Long usuarioId) {
        public static Resposta de(Alerta a) {
            return new Resposta(a.getId(), a.getTitulo(), a.getMensagem(), a.getNivel(),
                    a.getEnviadoWhatsapp(), a.getCriadoEm(), a.getUsuario().getId());
        }
    }
}
