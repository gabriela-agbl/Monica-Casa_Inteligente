package com.monica.casa.controller;

import com.monica.casa.dto.ConfiguracaoDTO;
import com.monica.casa.service.ConfiguracaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Preferências da central (somente ADMIN — /api/admin/**). */
@RestController
@RequestMapping("/api/admin/configuracoes")
@RequiredArgsConstructor
public class ConfiguracaoController {

    private final ConfiguracaoService configuracaoService;

    @GetMapping
    public List<ConfiguracaoDTO.Resposta> listar() {
        return configuracaoService.listar();
    }

    @GetMapping("/{chave}")
    public ConfiguracaoDTO.Resposta obter(@PathVariable String chave) {
        return configuracaoService.obter(chave);
    }

    /** Cria ou atualiza a configuração da chave informada. */
    @PutMapping("/{chave}")
    public ConfiguracaoDTO.Resposta salvar(@PathVariable String chave,
                                           @Valid @RequestBody ConfiguracaoDTO.Requisicao req) {
        return configuracaoService.salvar(chave, req);
    }

    @DeleteMapping("/{chave}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String chave) {
        configuracaoService.remover(chave);
    }
}
