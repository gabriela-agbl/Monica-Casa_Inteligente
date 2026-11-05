package com.monica.casa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Entity
@Table(name = "rotinas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rotina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalTime horarioExecucao;

    @Column(nullable = false)
    private Boolean ativa;

    private String acao; // Descrição da automação disparada
}