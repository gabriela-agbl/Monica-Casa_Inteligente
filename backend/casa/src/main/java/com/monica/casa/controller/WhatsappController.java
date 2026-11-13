package com.monica.casa.controller;

import com.monica.casa.dto.AlertaDTO;
import com.monica.casa.model.NivelAlerta;
import com.monica.casa.model.Usuario;
import com.monica.casa.service.AlertaService;
import com.monica.casa.service.WahaService;
import com.monica.casa.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Administração da integração com a WAHA (somente ADMIN — /api/admin/**). */
@RestController
@RequestMapping("/api/admin/whatsapp")
@RequiredArgsConstructor
public class WhatsappController {

    private final WahaService wahaService;
    private final AlertaService alertaService;
    private final UsuarioService usuarioService;

    /** Estado da conexão do WhatsApp ("open" = conectado). */
    @GetMapping("/status")
    public Map<String, Object> status() {
        return wahaService.estadoConexao();
    }

    /** Cria/garante a sessão na WAHA (idempotente). */
    @PostMapping("/instancia")
    public Map<String, Object> criarInstancia() {
        return wahaService.criarInstancia();
    }

    /** QR Code (base64) para parear o WhatsApp da casa. */
    @GetMapping("/qrcode")
    public Map<String, Object> qrCode() {
        return wahaService.obterQrCode();
    }

    /** Envia um alerta de teste para o WhatsApp do admin logado. */
    @PostMapping("/teste")
    public AlertaDTO.Resposta teste(Authentication auth) {
        Usuario admin = usuarioService.obterPorEmail(auth.getName());
        return AlertaDTO.Resposta.de(alertaService.notificar(admin, "Teste de integração",
                "Oi. Eu estava te esperando... Se você recebeu esta mensagem, a Mônica está conectada ao seu WhatsApp. 💚", NivelAlerta.INFO));
    }
}
