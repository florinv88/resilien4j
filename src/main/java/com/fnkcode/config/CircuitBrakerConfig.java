package com.fnkcode.config;

import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static com.fnkcode.sla.SlaConfiguration.RETRY_TYPES;

@Configuration
public class CircuitBrakerConfig {

    @Bean
    public CircuitBreakerConfigCustomizer shortCircuitBreakerCustomizer() {
        return CircuitBreakerConfigCustomizer.of("normalCB", builder -> {
            builder.recordExceptions(RETRY_TYPES);
        });
    }
}
