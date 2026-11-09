package com.monica.casa.exception;

/** Violação de regra de negócio / dado inválido. Mapeada para HTTP 400. */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
