package com.monica.casa.controller;

import com.monica.casa.dto.DispositivoDTO;
import com.monica.casa.dto.RotinaDTO;
import com.monica.casa.service.RotinaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Agendamento de rotinas domésticas (RF03). Somente ADMIN. */
@RestController
@RequestMapping("/api/rotinas")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class RotinaController {

    private final RotinaService rotinaService;

    @GetMapping
    public List<RotinaDTO.Resposta> listar() {
        return rotinaService.listar();
    }

    @GetMapping("/{id}")
    public RotinaDTO.Resposta buscar(@PathVariable Long id) {
        return rotinaService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RotinaDTO.Resposta criar(@Valid @RequestBody RotinaDTO.Requisicao req) {
        return rotinaService.criar(req);
    }

    @PutMapping("/{id}")
    public RotinaDTO.Resposta atualizar(@PathVariable Long id, @Valid @RequestBody RotinaDTO.Requisicao req) {
        return rotinaService.atualizar(id, req);
    }

    /** Ativa/pausa a rotina: body {"ativo": false}. */
    @PatchMapping("/{id}/ativo")
    public RotinaDTO.Resposta alterarAtivo(@PathVariable Long id, @Valid @RequestBody RotinaDTO.AlterarAtivo req) {
        return rotinaService.alterarAtivo(id, req.ativo());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        rotinaService.remover(id);
    }

    /** Dispara a rotina agora, sem esperar o horário (ótimo para a demonstração na ExpoTech). */
    @PostMapping("/{id}/executar")
    public DispositivoDTO.Resposta executarAgora(@PathVariable Long id) {
        return rotinaService.executar(id);
    }
}
