package com.monica.casa.service;

import com.monica.casa.config.EvolutionProperties;
import com.monica.casa.exception.IntegracaoExternaException;
import com.monica.casa.exception.RegraNegocioException;
import com.monica.casa.util.TelefoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Integração HTTP com a Evolution API (WhatsApp).
 * Envio de mensagens é "melhor esforço": falha na API nunca derruba a regra de negócio.
 */
@Slf4j
@Service
public class EvolutionApiService {

    private static final ParameterizedTypeReference<Map<String, Object>> MAPA =
            new ParameterizedTypeReference<>() {
            };

    private final EvolutionProperties props;
    private final RestClient client;

    public EvolutionApiService(EvolutionProperties props, @Qualifier("evolutionRestClient") RestClient client) {
        this.props = props;
        this.client = client;
    }

    /**
     * Envia uma mensagem de texto: POST /message/sendText/{instance}.
     *
     * @return true se a Evolution API aceitou a mensagem; false se desabilitada ou em caso de falha
     */
    public boolean enviarTexto(String telefone, String texto) {
        if (!props.isEnabled()) {
            log.info("Evolution API desabilitada (evolution.enabled=false); mensagem não enviada.");
            return false;
        }
        String numero = TelefoneUtil.normalizar(telefone);
        if (!TelefoneUtil.valido(numero)) {
            log.warn("Telefone inválido para WhatsApp: {}", TelefoneUtil.mascarar(numero));
            return false;
        }
        try {
            client.post()
                    .uri("/message/sendText/{instance}", props.getInstance())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("number", numero, "text", texto))
                    .retrieve()
                    .toBodilessEntity();
            log.info("WhatsApp enviado para {}", TelefoneUtil.mascarar(numero));
            return true;
        } catch (RestClientException e) {
            log.warn("Falha ao enviar WhatsApp para {}: {}", TelefoneUtil.mascarar(numero), e.getMessage());
            return false;
        }
    }

    /** GET /instance/connectionState/{instance}: informa se o WhatsApp está conectado ("open"). */
    public Map<String, Object> estadoConexao() {
        exigirHabilitada();
        try {
            return client.get()
                    .uri("/instance/connectionState/{instance}", props.getInstance())
                    .retrieve()
                    .body(MAPA);
        } catch (RestClientException e) {
            throw new IntegracaoExternaException("Evolution API indisponível ou instância inexistente: " + e.getMessage(), e);
        }
    }

    /** POST /instance/create: cria a instância do WhatsApp (uma única vez). */
    public Map<String, Object> criarInstancia() {
        exigirHabilitada();
        try {
            return client.post()
                    .uri("/instance/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "instanceName", props.getInstance(),
                            "qrcode", true,
                            "integration", "WHATSAPP-BAILEYS"))
                    .retrieve()
                    .body(MAPA);
        } catch (RestClientException e) {
            throw new IntegracaoExternaException("Não foi possível criar a instância na Evolution API: " + e.getMessage(), e);
        }
    }

    /** GET /instance/connect/{instance}: devolve o QR Code (base64) para parear o WhatsApp. */
    public Map<String, Object> obterQrCode() {
        exigirHabilitada();
        try {
            return client.get()
                    .uri("/instance/connect/{instance}", props.getInstance())
                    .retrieve()
                    .body(MAPA);
        } catch (RestClientException e) {
            throw new IntegracaoExternaException("Não foi possível obter o QR Code: " + e.getMessage(), e);
        }
    }

    private void exigirHabilitada() {
        if (!props.isEnabled()) {
            throw new RegraNegocioException("Integração com a Evolution API está desabilitada (evolution.enabled=false).");
        }
    }
}
