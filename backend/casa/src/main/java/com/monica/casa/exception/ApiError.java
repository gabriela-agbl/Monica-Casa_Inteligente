package com.monica.casa.exception;

import java.time.LocalDateTime;
import java.util.Map;

/** Corpo padrão de erro devolvido pela API. */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        Map<String, String> campos) {
}
