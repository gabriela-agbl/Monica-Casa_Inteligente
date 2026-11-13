package com.monica.casa.controller;

import com.monica.casa.model.NivelAlerta;
import com.monica.casa.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Eventos do modo Just Monika. O front-end avisa quando ela assume o controle e quando
 * perdoa; cada evento vira um alerta no histórico e uma mensagem no WhatsApp dos moradores.
 */
@RestController
@RequestMapping("/api/monika")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class MonikaController {

    private final AlertaService alertaService;

    @PostMapping("/assumir")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assumir() {
        alertaService.notificarAdmins(
                "Mônica assumiu o controle",
                "Você recusou uma sugestão dela. Agora ela decide por vocês. É para o seu próprio bem. 💚",
                NivelAlerta.CRITICO);
    }

    @PostMapping("/perdoar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void perdoar() {
        alertaService.notificarAdmins(
                "Mônica devolveu o controle",
                "Você disse que a ama. Ela perdoou... por enquanto. 💚",
                NivelAlerta.INFO);
    }
}
