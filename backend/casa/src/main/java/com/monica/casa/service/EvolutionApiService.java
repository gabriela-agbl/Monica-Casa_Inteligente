
package com.monica.casa.service;

import com.monica.casa.config.EvolutionProperties;
import com.monica.casa.exception.IntegracaoExternaException;
import com.monica.casa.exception.RegraNegocioException;
import com.monica.casa.util.TelefoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class EvolutionApiService {

    private static final ParameterizedTypeReference<Map<String, Object>> MAPA =
            new ParameterizedTypeReference<>() {};

    private final EvolutionProperties props;
    private final RestClient client;

    public EvolutionApiService(
            EvolutionProperties props,
            @Qualifier("evolutionRestClient") RestClient client) {
        this.props = props;
        this.client = client;
    }

    /**
     * Envia uma mensagem de texto pela WAHA.
     */
    public boolean enviarTexto(String telefone, String texto) {

        if (!props.isEnabled()) {
            log.info("WAHA desabilitada. Mensagem não enviada.");
            return false;
        }

        String numero = TelefoneUtil.normalizar(telefone);

        if (!TelefoneUtil.valido(numero)) {
            log.warn("Telefone inválido: {}",
                    TelefoneUtil.mascarar(numero));
            return false;
        }

        try {
            client.post()
                    .uri("/sendText")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "session", props.getInstance(),
                            "chatId", numero + "@c.us",
                            "text", texto
                    ))
                    .retrieve()
                    .toBodilessEntity();

            log.info("WhatsApp enviado para {}",
                    TelefoneUtil.mascarar(numero));

            return true;

        } catch (RestClientException e) {
            log.warn("Falha ao enviar WhatsApp para {}: {}",
                    TelefoneUtil.mascarar(numero), e.getMessage());
            return false;
        }
    }

    /**
     * Consulta o estado da sessão na WAHA.
     */
    public Map<String, Object> estadoConexao() {
        exigirHabilitada();

        try {
            Map<String, Object> resposta = client.get()
                    .uri("/sessions/{session}", props.getInstance())
                    .retrieve()
                    .body(MAPA);

            if (resposta == null) {
                throw new IntegracaoExternaException(
                        "WAHA retornou uma resposta vazia.", null);
            }

            Map<String, Object> resultado = new HashMap<>(resposta);

            String status = String.valueOf(resposta.get("status"));

            // Mantém compatibilidade com o frontend atual.
            if ("WORKING".equalsIgnoreCase(status)) {
                resultado.put("state", "open");
            } else if ("STARTING".equalsIgnoreCase(status)) {
                resultado.put("state", "connecting");
            } else {
                resultado.put("state", status.toLowerCase());
            }

            return resultado;

        } catch (RestClientException e) {
            throw new IntegracaoExternaException(
                    "WAHA indisponível ou sessão inexistente: "
                            + e.getMessage(), e);
        }
    }

    /**
     * Cria a sessão caso ainda não exista.
     * A WAHA inicia a sessão automaticamente após a criação.
     */
    public Map<String, Object> criarInstancia() {
        exigirHabilitada();

        try {
            // Primeiro verifica se a sessão já existe.
            try {
                Map<String, Object> existente = client.get()
                        .uri("/sessions/{session}", props.getInstance())
                        .retrieve()
                        .body(MAPA);

                if (existente != null) {
                    log.info("Sessão WAHA '{}' já existe.",
                            props.getInstance());
                    return existente;
                }

            } catch (HttpClientErrorException.NotFound e) {
                log.info("Sessão não encontrada. Criando nova sessão.");
            }

            // Cria a sessão.
            Map<String, Object> resposta = client.post()
                    .uri("/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("name", props.getInstance()))
                    .retrieve()
                    .body(MAPA);

            log.info("Sessão WAHA '{}' criada.",
                    props.getInstance());

            return resposta;

        } catch (RestClientException e) {
            throw new IntegracaoExternaException(
                    "Não foi possível criar a sessão na WAHA: "
                            + e.getMessage(), e);
        }
    }

    /**
     * Obtém o QR Code em Base64 para o frontend.
     */
    public Map<String, Object> obterQrCode() {
        exigirHabilitada();

        try {
            Map<String, Object> resposta = client.get()
                    .uri("/{session}/auth/qr?format=image",
                            props.getInstance())
                    .header(HttpHeaders.ACCEPT,
                            MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .body(MAPA);

            if (resposta == null || resposta.get("data") == null) {
                throw new IntegracaoExternaException(
                        "A WAHA não retornou um QR Code.", null);
            }

            String mimetype = String.valueOf(
                    resposta.getOrDefault("mimetype", "image/png"));

            String base64 = String.valueOf(resposta.get("data"));

            // O frontend atual espera o campo "base64".
            Map<String, Object> resultado = new HashMap<>(resposta);
            resultado.put("base64",
                    "data:" + mimetype + ";base64," + base64);

            return resultado;

        } catch (RestClientException e) {
            throw new IntegracaoExternaException(
                    "Não foi possível obter o QR Code da WAHA: "
                            + e.getMessage(), e);
        }
    }

    private void exigirHabilitada() {
        if (!props.isEnabled()) {
            throw new RegraNegocioException(
                    "Integração com a WAHA está desabilitada.");
        }
    }
}