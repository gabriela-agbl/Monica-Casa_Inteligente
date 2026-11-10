package com.monica.casa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuracoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuracoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String chave; // Ex: MODO_AUTOMATICO, NOTIFICACOES_WHATSAPP, TEMPERATURA_PADRAO

    @Column(nullable = false)
    private String valor; // Ex: true, ligado, 22.5
}