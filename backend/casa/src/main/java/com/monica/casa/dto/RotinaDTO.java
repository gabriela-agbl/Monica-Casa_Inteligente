package com.monica.casa.dto;

import com.monica.casa.model.AcaoRotina;
import com.monica.casa.model.Rotina;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public final class RotinaDTO {

    private RotinaDTO() {
    }

    public record Requisicao(
            @NotBlank(message = "Nome é obrigatório") @Size(max = 100, message = "Nome deve ter até 100 caracteres") String nome,
            @NotNull(message = "Horário é obrigatório (formato HH:mm)") LocalTime horario,
            @NotBlank(message = "Informe os dias, ex.: SEG,TER,QUA") @Size(max = 50) String diasSemana,
            @NotNull(message = "Ação é obrigatória (LIGAR ou DESLIGAR)") AcaoRotina acao,
            @NotNull(message = "dispositivoId é obrigatório") Long dispositivoId,
            Boolean ativo) {
    }

    public record AlterarAtivo(
            @NotNull(message = "Informe 'ativo': true ou false") Boolean ativo) {
    }

    public record Resposta(Long id, String nome, LocalTime horario, String diasSemana, AcaoRotina acao,
                           Boolean ativo, Long dispositivoId, String dispositivoNome) {
        public static Resposta de(Rotina r) {
            return new Resposta(r.getId(), r.getNome(), r.getHorario(), r.getDiasSemana(), r.getAcao(),
                    r.getAtivo(), r.getDispositivo().getId(), r.getDispositivo().getNome());
        }
    }
}
