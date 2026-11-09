package com.monica.casa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/** Cliente HTTP (RestClient) já apontado para a Evolution API, com apikey e timeouts. */
@Configuration
public class EvolutionClientConfig {

    @Bean
    public RestClient evolutionRestClient(EvolutionProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(props.getTimeoutSeconds()));
        factory.setReadTimeout(Duration.ofSeconds(props.getTimeoutSeconds()));

        return RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader("apikey", props.getApiKey())
                .requestFactory(factory)
                .build();
    }
}
