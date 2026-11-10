package com.monica.casa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dispositivos")
@Getter
@Setter
@NoArgsConstructor
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
