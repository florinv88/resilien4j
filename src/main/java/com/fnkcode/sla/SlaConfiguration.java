package com.fnkcode.sla;

import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import io.github.resilience4j.core.IntervalFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class SlaConfiguration {

    @Value("${custom-sla.shortRetry.initial-interval}")
    private Duration initialInterval;

    @Value("${custom-sla.shortRetry.multiplier}")
    private double multiplier;

    @Value("${custom-sla.shortRetry.randomization-factor}")
    private double randomizationFactor;


    @SuppressWarnings("unchecked")
    private static final Class<? extends Throwable>[] RETRY_TYPES = new Class[] {
            java.io.IOException.class,
            java.net.SocketTimeoutException.class,
            org.springframework.web.client.HttpServerErrorException.class,
            org.springframework.web.client.ResourceAccessException.class
    };

    @Bean
    public RetryConfigCustomizer shortRetryCustomizer() {
        return RetryConfigCustomizer.of("shortRetry", builder -> {
            builder.retryOnException(new SlaRetryPredicate(RETRY_TYPES));
            builder.intervalFunction(IntervalFunction.ofExponentialRandomBackoff(initialInterval, multiplier, randomizationFactor)
            );
        });
    }


    @Bean
    public RetryConfigCustomizer normalRetryCustomizer() {
        return RetryConfigCustomizer.of("normalRetry", builder -> {
            builder.retryOnException(new SlaRetryPredicate(RETRY_TYPES));
            builder.intervalFunction(IntervalFunction.ofExponentialRandomBackoff(100, multiplier, randomizationFactor)
            );
        });
    }
}