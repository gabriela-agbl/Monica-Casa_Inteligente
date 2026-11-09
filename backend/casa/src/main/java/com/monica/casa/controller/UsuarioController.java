package com.monica.casa.controller;

import com.monica.casa.dto.UsuarioDTO;
import com.monica.casa.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Gestão de usuários e permissões (somente ADMIN — ver SecurityConfig: /api/admin/**). */
@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioDTO.Resposta> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public UsuarioDTO.Resposta buscar(@PathVariable Long id) {
        return usuarioService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO.Resposta criar(@Valid @RequestBody UsuarioDTO.CriacaoAdmin req) {
        return usuarioService.criarPorAdmin(req);
    }

    @PutMapping("/{id}")
    public UsuarioDTO.Resposta atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO.Atualizacao req) {
        return usuarioService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, Authentication auth) {
        usuarioService.remover(id, auth.getName());
    }
}
