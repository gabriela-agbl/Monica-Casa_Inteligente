package com.monica.casa.service;

import com.monica.casa.dto.AlertaDTO;
import com.monica.casa.model.Alerta;
import com.monica.casa.model.NivelAlerta;
import com.monica.casa.model.Perfil;
import com.monica.casa.model.Usuario;
import com.monica.casa.repository.AlertaRepository;
import com.monica.casa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Central de alertas: grava no painel e, se habilitado, dispara WhatsApp via Evolution API. */
@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConfiguracaoService configuracaoService;
    private final EvolutionApiService evolutionApiService;

    /** Notifica todos os moradores (ADMIN). */
    @Transactional
    public void notificarAdmins(String titulo, String mensagem, NivelAlerta nivel) {
        for (Usuario admin : usuarioRepository.findByPerfil(Perfil.ADMIN)) {
            notificar(admin, titulo, mensagem, nivel);
        }
    }

    @Transactional
    public Alerta notificar(Usuario destino, String titulo, String mensagem, NivelAlerta nivel) {
        Alerta alerta = new Alerta();
        alerta.setUsuario(destino);
        alerta.setTitulo(titulo);
        alerta.setMensagem(mensagem);
        alerta.setNivel(nivel);
        alerta.setEnviadoWhatsapp(false);
        alerta = alertaRepository.save(alerta);

        if (configuracaoService.getBoolean(ConfiguracaoService.NOTIFICACOES_WHATSAPP, true)) {
            boolean enviado = evolutionApiService.enviarTexto(destino.getTelefone(), formatar(titulo, mensagem, nivel));
            alerta.setEnviadoWhatsapp(enviado);
            alerta = alertaRepository.save(alerta);
        }
        return alerta;
    }

    @Transactional(readOnly = true)
    public List<AlertaDTO.Resposta> listar(int limite) {
        return alertaRepository.findRecentes(PageRequest.of(0, limite)).stream()
                .map(AlertaDTO.Resposta::de).toList();
    }

    private String formatar(String titulo, String mensagem, NivelAlerta nivel) {
        String icone = switch (nivel) {
            case INFO -> "ℹ️";
            case ALERTA -> "⚠️";
            case CRITICO -> "🚨";
        };
        return "*🏠 Mônica* | " + icone + " *" + titulo + "*\n" + mensagem;
    }
}
