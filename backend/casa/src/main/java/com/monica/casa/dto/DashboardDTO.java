package com.monica.casa.dto;

public final class DashboardDTO {

    private DashboardDTO() {
    }

    public record Resumo(long totalDispositivos, long dispositivosLigados, long rotinasAtivas,
                         long alertasUltimas24h, long totalUsuarios) {
    }
}
