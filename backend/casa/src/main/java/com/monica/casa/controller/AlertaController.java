package com.monica.casa.controller;

import com.monica.casa.dto.AlertaDTO;
import com.monica.casa.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Central de alertas e notificações enviadas. Somente ADMIN. */
@RestController
@RequestMapping("/api/alertas")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    public List<AlertaDTO.Resposta> listar(@RequestParam(defaultValue = "50") int limite) {
        return alertaService.listar(Math.max(1, Math.min(limite, 200)));
    }
}
