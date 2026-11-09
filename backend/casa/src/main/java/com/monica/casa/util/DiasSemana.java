package com.monica.casa.util;

import com.monica.casa.exception.RegraNegocioException;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/** Conversão entre a coluna dias_semana ("SEG,TER,...") e {@link DayOfWeek}. */
public final class DiasSemana {

    /** Índice 0 = segunda-feira (DayOfWeek.MONDAY.getValue() - 1). */
    private static final List<String> ORDEM = List.of("SEG", "TER", "QUA", "QUI", "SEX", "SAB", "DOM");

    private DiasSemana() {
    }

    /** Valida, remove duplicados e ordena (SEG..DOM). Lança RegraNegocioException se inválido. */
    public static String normalizar(String entrada) {
        if (entrada == null || entrada.isBlank()) {
            throw new RegraNegocioException("Informe ao menos um dia da semana (SEG,TER,QUA,QUI,SEX,SAB,DOM).");
        }
        Set<String> dias = new HashSet<>();
        for (String parte : entrada.split(",")) {
            String sigla = parte.trim().toUpperCase(Locale.ROOT);
            if (!ORDEM.contains(sigla)) {
                throw new RegraNegocioException("Dia da semana inválido: '" + parte.trim()
                        + "'. Use SEG,TER,QUA,QUI,SEX,SAB,DOM separados por vírgula.");
            }
            dias.add(sigla);
        }
        return ORDEM.stream().filter(dias::contains).collect(Collectors.joining(","));
    }

    public static boolean contem(String diasSemana, DayOfWeek dia) {
        String sigla = ORDEM.get(dia.getValue() - 1);
        return Arrays.asList(diasSemana.split(",")).contains(sigla);
    }
}
