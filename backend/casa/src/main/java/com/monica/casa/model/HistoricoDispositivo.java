package com.monica.casa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Registro de cada mudança de estado de um dispositivo (histórico de uso). */
@Entity
@Table(name = "historico_dispositivos")
@Getter
@Setter
@NoArgsConstructor
public class HistoricoDispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

    /** Estado assumido: true = ligou, false = desligou. */
    @Column(nullable = false)
    private Boolean ligado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrigemAcionamento origem;

    /** Nome de quem acionou (ou "Rotina: ..."). Texto, para sobreviver à remoção do usuário. */
    @Column(name = "acionado_por", length = 150)
    private String acionadoPor;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void aoCriar() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
