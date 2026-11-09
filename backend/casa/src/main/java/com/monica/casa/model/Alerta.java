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
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
<<<<<<< HEAD
@Data
@NoArgsConstructor
@AllArgsConstructor
=======
@Getter
@Setter
@NoArgsConstructor
>>>>>>> 8adff7e (Implementa backend Spring Boot da Monica Casa Inteligente)
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean lido = false;
}
=======
    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelAlerta nivel;

    @Column(name = "enviado_whatsapp", nullable = false)
    private Boolean enviadoWhatsapp = false;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    /** Destinatário do alerta. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    void aoCriar() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
>>>>>>> 8adff7e (Implementa backend Spring Boot da Monica Casa Inteligente)
