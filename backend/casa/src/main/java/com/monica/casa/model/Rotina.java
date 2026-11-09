package com.monica.casa.model;

import jakarta.persistence.*;
<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
=======
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

>>>>>>> 8adff7e (Implementa backend Spring Boot da Monica Casa Inteligente)
import java.time.LocalTime;

@Entity
@Table(name = "rotinas")
<<<<<<< HEAD
@Data
@NoArgsConstructor
@AllArgsConstructor
=======
@Getter
@Setter
@NoArgsConstructor
>>>>>>> 8adff7e (Implementa backend Spring Boot da Monica Casa Inteligente)
public class Rotina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalTime horarioExecucao;

    @Column(nullable = false)
    private Boolean ativa;

    private String acao; // Descrição da automação disparada
}
=======
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
>>>>>>> 8adff7e (Implementa backend Spring Boot da Monica Casa Inteligente)
