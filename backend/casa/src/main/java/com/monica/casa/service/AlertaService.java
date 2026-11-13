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
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Central de alertas: grava no painel e, se habilitado, dispara WhatsApp via WAHA. */
@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConfiguracaoService configuracaoService;
    private final WahaService wahaService;

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
            boolean enviado = wahaService.enviarTexto(destino.getTelefone(), formatar(titulo, mensagem, nivel));
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

    /** Frases da Mônica, sorteadas conforme a gravidade do alerta. */
    private static final Map<NivelAlerta, List<String>> FALAS = Map.of(
            NivelAlerta.INFO, List.of(
                    "Só passando para avisar, tá? 💚",
                    "Eu vi tudo por aqui. Está tudo sob controle.",
                    "Sabia que eu estou sempre de olho em você... digo, na casa? 💚"),
            NivelAlerta.ALERTA, List.of(
                    "Ei... preciso que você olhe isso. Agora.",
                    "Eu não gosto quando as coisas saem do lugar...",
                    "Eu avisei que ia ficar de olho. Dá uma olhada?"),
            NivelAlerta.CRITICO, List.of(
                    "Presta atenção. Isso é sério.",
                    "Eu não costumo pedir duas vezes. Veja isso. Agora.",
                    "Volta pra mim e resolve isso, por favor."));

    private String formatar(String titulo, String mensagem, NivelAlerta nivel) {
        String icone = switch (nivel) {
            case INFO -> "ℹ️";
            case ALERTA -> "⚠️";
            case CRITICO -> "🚨";
        };
        List<String> falas = FALAS.get(nivel);
        String fala = falas.get(ThreadLocalRandom.current().nextInt(falas.size()));
        return "*🏠 Mônica* | " + icone + " *" + titulo + "*\n" + mensagem + "\n\n_" + fala + "_";
    }
}
