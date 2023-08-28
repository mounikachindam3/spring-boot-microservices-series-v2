/*** Licensed under Apache-2.0 2023 ***/
package com.example.catalogservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class CircuitBreakerConfig {

    @Bean
    RegistryEventConsumer<CircuitBreaker> myRegistryEventConsumer() {

        return new RegistryEventConsumer<>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
                entryAddedEvent
                        .getAddedEntry()
                        .getEventPublisher()
                        .onEvent(event -> log.info(event.toString()));
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {
                entryRemoveEvent
                        .getRemovedEntry()
                        .getEventPublisher()
                        .onEvent(event -> log.info(event.toString()));
            }

            @Override
            public void onEntryReplacedEvent(
                    EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
                entryReplacedEvent
                        .getOldEntry()
                        .getEventPublisher()
                        .onEvent(event -> log.info("Old Entry :{}", event));
                entryReplacedEvent
                        .getNewEntry()
                        .getEventPublisher()
                        .onEvent(event -> log.info("New Entry :{}", event));
            }
        };
    }

    @Bean
    RegistryEventConsumer<Retry> myRetryRegistryEventConsumer() {

        return new RegistryEventConsumer<>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<Retry> entryAddedEvent) {
                entryAddedEvent
                        .getAddedEntry()
                        .getEventPublisher()
                        .onEvent(event -> log.info(event.toString()));
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<Retry> entryRemoveEvent) {}

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<Retry> entryReplacedEvent) {}
        };
    }
}