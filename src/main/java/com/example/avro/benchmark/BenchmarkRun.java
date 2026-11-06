package com.example.avro.benchmark;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Pomiar jednego formatu: konsument zgłasza każdą odebraną wiadomość, a wynik liczymy, gdy przyjdą wszystkie.
 */
class BenchmarkRun {

    public record Summary(String format,
                          int messages,
                          double bytesPerMessage,
                          long totalBytes,
                          double totalTimeMs,
                          long messagesPerSecond,
                          double avgLatencyMs,
                          double p50LatencyMs,
                          double p95LatencyMs,
                          double maxLatencyMs) {
    }

    private final String format;
    private final int count;
    private final long startedAt = nowMicros();
    private final List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong totalBytes = new AtomicLong();
    private final CountDownLatch remaining;
    private volatile long lastReceivedAt;

    BenchmarkRun(String format, int count) {
        this.format = format;
        this.count = count;
        this.remaining = new CountDownLatch(count);
    }

    void received(long sentAt, int bytes) {
        lastReceivedAt = nowMicros();
        latencies.add(lastReceivedAt - sentAt);
        totalBytes.addAndGet(bytes);
        remaining.countDown();
    }

    Summary awaitSummary() throws InterruptedException {
        if (!remaining.await(2, TimeUnit.MINUTES)) {
            throw new IllegalStateException(format + ": nie odebrano wszystkich wiadomości");
        }
        List<Long> sorted = latencies.stream()
                                     .sorted()
                                     .toList();
        long totalTime = lastReceivedAt - startedAt;
        return new Summary(
                format,
                count,
                (double) totalBytes.get() / count,
                totalBytes.get(),
                millis(totalTime),
                Math.round(count / (totalTime / 1_000_000.0)),
                millis(sorted.stream()
                             .mapToLong(Long::longValue)
                             .average()
                             .orElse(0)),
                millis(percentile(sorted, 0.50)),
                millis(percentile(sorted, 0.95)),
                millis(sorted.getLast()));
    }

    private static long percentile(List<Long> sorted, double p) {
        return sorted.get((int) Math.ceil(p * sorted.size()) - 1);
    }

    static long nowMicros() {
        return toMicros(Instant.now());
    }

    static long toMicros(Instant instant) {
        return ChronoUnit.MICROS.between(Instant.EPOCH, instant);
    }

    private static double millis(double micros) {
        return Math.round(micros / 10) / 100.0;
    }

}
