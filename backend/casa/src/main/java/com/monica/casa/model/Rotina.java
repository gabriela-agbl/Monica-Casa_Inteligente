package com.monica.casa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "rotinas")
@Getter
@Setter
@NoArgsConstructor
public class Rotina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    /** Horário (HH:mm) em que a rotina dispara. */
    @Column(name = "horario", nullable = false)
    private LocalTime horario;

    /** Dias separados por vírgula, ex.: "SEG,TER,QUA,QUI,SEX". */
    @Column(name = "dias_semana", nullable = false, length = 50)
    private String diasSemana;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AcaoRotina acao;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;
}
