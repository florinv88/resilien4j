package com.fnkcode.utils;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Component
@SuppressWarnings("unused")
public class RetryEventLogger {

//    private static final Logger logger = LoggerFactory.getLogger(RetryEventLogger.class);
//
//    public RetryEventLogger(RetryRegistry retryRegistry) {
//        retryRegistry.getAllRetries().forEach(this::registerEventConsumers);
//    }
//
//    private void registerEventConsumers(Retry retry) {
//        retry.getEventPublisher()
//                .onRetry(event -> logger.info("Retry: attempt {} for {}",
//                        event.getNumberOfRetryAttempts(), event.getName()))
//                .onSuccess(event -> logger.info("Success after {} attempts for {}",
//                        event.getNumberOfRetryAttempts(), event.getName()))
//                .onError(event -> logger.error("Failed after {} attempts for {}",
//                        event.getNumberOfRetryAttempts(), event.getName()));
//    }
}