package com.monica.casa.controller;

import com.monica.casa.dto.DispositivoDTO;
import com.monica.casa.service.DispositivoService;
import com.monica.casa.util.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD de dispositivos (cadastro, status, histórico).
 * Leitura e acionamento: ADMIN e COMUM (este só nos liberados). Escrita/histórico: ADMIN.
 */
@RestController
@RequestMapping("/api/dispositivos")
@RequiredArgsConstructor
public class DispositivoController {

    private final DispositivoService dispositivoService;

    @GetMapping
    public List<DispositivoDTO.Resposta> listar(Authentication auth) {
        return dispositivoService.listar(AuthUtil.isAdmin(auth));
    }

    @GetMapping("/{id}")
    public DispositivoDTO.Resposta buscar(@PathVariable Long id, Authentication auth) {
        return dispositivoService.buscar(id, AuthUtil.isAdmin(auth));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public DispositivoDTO.Resposta criar(@Valid @RequestBody DispositivoDTO.Requisicao req, Authentication auth) {
        return dispositivoService.criar(req, auth.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DispositivoDTO.Resposta atualizar(@PathVariable Long id, @Valid @RequestBody DispositivoDTO.Requisicao req) {
        return dispositivoService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        dispositivoService.remover(id);
    }

    /** Liga/desliga: body {"ligado": true}. */
    @PatchMapping("/{id}/status")
    public DispositivoDTO.Resposta alterarStatus(@PathVariable Long id,
                                                 @Valid @RequestBody DispositivoDTO.AlterarStatus req,
                                                 Authentication auth) {
        return dispositivoService.alterarStatus(id, req.ligado(), auth.getName(), AuthUtil.isAdmin(auth));
    }

    @GetMapping("/{id}/historico")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DispositivoDTO.Historico> historico(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "50") int limite) {
        return dispositivoService.historico(id, limitar(limite));
    }

    /** Histórico de acionamentos de toda a casa (mais recentes primeiro). */
    @GetMapping("/historico")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DispositivoDTO.Historico> historicoGeral(@RequestParam(defaultValue = "50") int limite) {
        return dispositivoService.historicoGeral(limitar(limite));
    }

    private int limitar(int limite) {
        return Math.max(1, Math.min(limite, 200));
    }
}
