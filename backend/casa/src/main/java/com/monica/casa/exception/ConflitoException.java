package com.monica.casa.exception;

/** Conflito com o estado atual (ex.: e-mail já cadastrado). Mapeada para HTTP 409. */
public class ConflitoException extends RuntimeException {
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
