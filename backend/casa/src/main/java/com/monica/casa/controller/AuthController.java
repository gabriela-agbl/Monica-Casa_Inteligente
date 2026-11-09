package com.monica.casa.controller;

import com.monica.casa.dto.UsuarioDTO;
import com.monica.casa.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    /** Cadastro público (RF01). Sempre cria perfil COMUM. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO.Resposta registrar(@Valid @RequestBody UsuarioDTO.Registro req) {
        return usuarioService.registrar(req);
    }

    /**
     * Login: o front envia e-mail/senha via HTTP Basic; se as credenciais forem válidas,
     * este endpoint devolve os dados e o perfil (ADMIN/COMUM) para montar a interface.
     */
    @GetMapping("/me")
    public UsuarioDTO.Resposta me(Authentication auth) {
        return usuarioService.buscarPorEmail(auth.getName());
    }
}
