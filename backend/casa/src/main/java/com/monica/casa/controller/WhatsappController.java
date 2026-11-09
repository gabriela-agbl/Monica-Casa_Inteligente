package com.monica.casa.controller;

import com.monica.casa.dto.AlertaDTO;
import com.monica.casa.model.NivelAlerta;
import com.monica.casa.model.Usuario;
import com.monica.casa.service.AlertaService;
import com.monica.casa.service.EvolutionApiService;
import com.monica.casa.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Administração da integração com a Evolution API (somente ADMIN — /api/admin/**). */
@RestController
@RequestMapping("/api/admin/whatsapp")
@RequiredArgsConstructor
public class WhatsappController {

    private final EvolutionApiService evolutionApiService;
    private final AlertaService alertaService;
    private final UsuarioService usuarioService;

    /** Estado da conexão do WhatsApp ("open" = conectado). */
    @GetMapping("/status")
    public Map<String, Object> status() {
        return evolutionApiService.estadoConexao();
    }

    /** Cria a instância na Evolution API (uma vez). */
    @PostMapping("/instancia")
    public Map<String, Object> criarInstancia() {
        return evolutionApiService.criarInstancia();
    }

    /** QR Code (base64) para parear o WhatsApp da casa. */
    @GetMapping("/qrcode")
    public Map<String, Object> qrCode() {
        return evolutionApiService.obterQrCode();
    }

    /** Envia um alerta de teste para o WhatsApp do admin logado. */
    @PostMapping("/teste")
    public AlertaDTO.Resposta teste(Authentication auth) {
        Usuario admin = usuarioService.obterPorEmail(auth.getName());
        return AlertaDTO.Resposta.de(alertaService.notificar(admin, "Teste de integração",
                "Se você recebeu esta mensagem, a Mônica está conectada ao seu WhatsApp. 💚", NivelAlerta.INFO));
    }
}
