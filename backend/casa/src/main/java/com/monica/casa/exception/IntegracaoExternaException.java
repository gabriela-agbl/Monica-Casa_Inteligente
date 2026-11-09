package com.monica.casa.exception;

/** Falha ao falar com um serviço externo (Evolution API). Mapeada para HTTP 502. */
public class IntegracaoExternaException extends RuntimeException {
    public IntegracaoExternaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
