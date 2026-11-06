package com.example.avro.benchmark;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** @param messageCount domyślna liczba wiadomości na format (application.yaml: benchmark.message-count) */
@ConfigurationProperties("benchmark")
public record BenchmarkProperties(int messageCount) {
}
