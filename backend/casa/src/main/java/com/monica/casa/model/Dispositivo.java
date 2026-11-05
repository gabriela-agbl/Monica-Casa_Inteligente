package com.monica.casa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dispositivos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String tipo; // Ex: LÂMPADA, SENSOR, CÂMERA, AR_CONDICIONADO

    @Column(nullable = false)
    private Boolean status; // true para ligado, false para desligado

    private String comodo; // Ex: Sala, Cozinha, Quarto
}