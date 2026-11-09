package com.monica.casa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Propriedades "evolution.*" do application.properties. */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "evolution")
public class EvolutionProperties {

    /** Desliga o envio real (útil em testes/demonstração sem WhatsApp conectado). */
    private boolean enabled = true;

    private String baseUrl = "http://localhost:8081";

    /** Mesma chave de AUTHENTICATION_API_KEY do docker-compose. */
    private String apiKey = "monica_secret_key_2050";

    /** Nome da instância do WhatsApp criada na Evolution API. */
    private String instance = "monica";

    private int timeoutSeconds = 5;
}
