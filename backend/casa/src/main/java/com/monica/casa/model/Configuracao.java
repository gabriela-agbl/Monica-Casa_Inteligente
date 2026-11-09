package com.monica.casa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configuracoes")
@Getter
@Setter
@NoArgsConstructor
public class Configuracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ex.: NOTIFICACOES_WHATSAPP, NOTIFICAR_ROTINAS, NOME_CASA. */
    @Column(nullable = false, unique = true, length = 100)
    private String chave;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String valor;

    @Column(length = 255)
    private String descricao;

    /** Opcional: preenchido quando a configuração é específica de um usuário. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
