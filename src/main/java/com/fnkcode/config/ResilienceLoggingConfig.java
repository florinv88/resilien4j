package com.fnkcode.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.event.RetryOnErrorEvent;
import io.github.resilience4j.retry.event.RetryOnIgnoredErrorEvent;
import io.github.resilience4j.retry.event.RetryOnRetryEvent;
import io.github.resilience4j.retry.event.RetryOnSuccessEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ResilienceLoggingConfig {

    private static final Logger logger = LoggerFactory.getLogger(ResilienceLoggingConfig.class);

    @Bean
    public RegistryEventConsumer<Retry> retryEventConsumer() {
        return new RegistryEventConsumer<>() {

            @Override
            public void onEntryAddedEvent(@NotNull EntryAddedEvent<Retry> entryAddedEvent) {
                bindLoggers(entryAddedEvent.getAddedEntry());
            }

            @Override
            public void onEntryRemovedEvent(@NotNull EntryRemovedEvent<Retry> entryRemoveEvent) {
                // No-op; logging is tied to the retry instance, so removal cleans up automatically
            }

            @Override
            public void onEntryReplacedEvent(@NotNull EntryReplacedEvent<Retry> entryReplacedEvent) {
                bindLoggers(entryReplacedEvent.getNewEntry());
            }
        };
    }

    @Bean
    public RegistryEventConsumer<CircuitBreaker> cbLog() {
        return new RegistryEventConsumer<>() {
            @Override
            public void onEntryAddedEvent(@NotNull EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onStateTransition(event -> logger.info("CB State Change: {}", event.getStateTransition()));
            }

            @Override
            public void onEntryRemovedEvent(@NotNull EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {

            }

            @Override
            public void onEntryReplacedEvent(@NotNull EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
                entryReplacedEvent.getNewEntry().getEventPublisher()
                        .onStateTransition(event -> logger.info("CB State Change: {}", event.getStateTransition()));

            }
        };
    }

    private void bindLoggers(Retry retry) {
        retry.getEventPublisher()
                .onRetry(this::logRetry)
                .onSuccess(this::logSuccess)
                .onIgnoredError(this::logIgnoredError)
                .onError(this::logError);
    }


    private void logRetry(RetryOnRetryEvent event) {
        Throwable cause = event.getLastThrowable();
        logger.info("Retry attempt {} waited {} for '{}' (cause: {})",
                event.getNumberOfRetryAttempts(),
                event.getWaitInterval().toMillis(),
                event.getName(),
                (cause != null ? cause.getMessage() : "none"));
    }

    private void logSuccess(RetryOnSuccessEvent event) {
        logger.info("Success after {} attempts for '{}'",
                event.getNumberOfRetryAttempts(), event.getName());
    }

    private void logError(RetryOnErrorEvent event) {
        Throwable cause = event.getLastThrowable();
        logger.error("Failed after {} attempts for '{}' (cause: {})",
                event.getNumberOfRetryAttempts(), event.getName(),
                (cause != null ? cause : "none"));
    }

    private void logIgnoredError(RetryOnIgnoredErrorEvent event) {
        logger.info("Ignored error after {} attempts for '{}'",
                event.getNumberOfRetryAttempts(), event.getName());
    }
}
