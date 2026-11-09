package com.monica.casa.dto;

import com.monica.casa.model.Configuracao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ConfiguracaoDTO {

    private ConfiguracaoDTO() {
    }

    public record Requisicao(
            @NotBlank(message = "Valor é obrigatório") String valor,
            @Size(max = 255, message = "Descrição deve ter até 255 caracteres") String descricao) {
    }

    public record Resposta(Long id, String chave, String valor, String descricao) {
        public static Resposta de(Configuracao c) {
            return new Resposta(c.getId(), c.getChave(), c.getValor(), c.getDescricao());
        }
    }
}
