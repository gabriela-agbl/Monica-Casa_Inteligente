package com.monica.casa.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Habilita @Scheduled. Desative com monica.scheduler.enabled=false (ex.: testes). */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "monica.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfig {
}
