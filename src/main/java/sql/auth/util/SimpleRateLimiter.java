package sql.auth.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SimpleRateLimiter {

    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public boolean allow(String key, int maxAttempts, int windowSeconds) {
        long now = Instant.now().getEpochSecond();
        WindowCounter current = counters.computeIfAbsent(key, k -> new WindowCounter(now, new AtomicInteger(0)));
        synchronized (current) {
            if (now - current.windowStart >= windowSeconds) {
                current.windowStart = now;
                current.counter.set(0);
            }
            return current.counter.incrementAndGet() <= maxAttempts;
        }
    }

    private static class WindowCounter {
        private long windowStart;
        private AtomicInteger counter;

        WindowCounter(long windowStart, AtomicInteger counter) {
            this.windowStart = windowStart;
            this.counter = counter;
        }
    }
}
