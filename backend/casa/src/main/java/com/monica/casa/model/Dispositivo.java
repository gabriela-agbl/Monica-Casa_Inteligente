package com.monica.casa.model;

import jakarta.persistence.*;
<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dispositivos")
@Data
@NoArgsConstructor
@AllArgsConstructor
=======
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dispositivos")
@Getter
@Setter
@NoArgsConstructor
>>>>>>> 8adff7e (Implementa backend Spring Boot da Monica Casa Inteligente)
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String tipo; // Ex: LÂMPADA, SENSOR, CÂMERA, AR_CONDICIONADO

    @Column(nullable = false)
    private Boolean status; // true para ligado, false para desligado

    private String comodo; // Ex: Sala, Cozinha, Quarto
}
=======
    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoDispositivo tipo;

    /** true = ligado, false = desligado. */
    @Column(nullable = false)
    private Boolean status = false;

    @Column(length = 50)
    private String comodo;

    /** Se true, usuários COMUM (convidados) podem acionar este dispositivo. */
    @Column(name = "acesso_convidado", nullable = false, columnDefinition = "boolean default false")
    private Boolean acessoConvidado = false;

    /** Admin que cadastrou o dispositivo. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
>>>>>>> 8adff7e (Implementa backend Spring Boot da Monica Casa Inteligente)
