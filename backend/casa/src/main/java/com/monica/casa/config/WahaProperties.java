
package com.monica.casa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "waha")
public class WahaProperties {

    private boolean enabled = true;

    private String baseUrl = "http://localhost:3000/api";

    private String apiKey = "";

    private String instance = "monica";

    private int timeoutSeconds = 30;
}