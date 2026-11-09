package com.monica.casa.model;

public enum AcaoRotina {
    LIGAR(true),
    DESLIGAR(false);

    private final boolean ligado;

    AcaoRotina(boolean ligado) {
        this.ligado = ligado;
    }

    /** Estado final que o dispositivo deve assumir ao executar a ação. */
    public boolean isLigado() {
        return ligado;
    }
}
