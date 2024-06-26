package poskryakov.resilience4j.demo;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;

@Service
public class CatchphraseService {

    private static final RateLimiter internalRateLimiter = initInternalRateLimiter();
    private static final Supplier<Integer> rateCounter = RateLimiter.decorateSupplier(internalRateLimiter, () -> 1);

    private static LocalDateTime downtimeEnd = LocalDateTime.now();

    // 1. Bulkhead

    public String getCatchphraseSlow() {
        // Response with random delay. High response time.
        Util.sleepRandom(7000, 10000);
        int randomIndex = ThreadLocalRandom.current().nextInt(Util.catchphrases.size());
        return Util.catchphrases.get(randomIndex);
    }

    // 2. RateLimiter

    public String getCatchphraseRateSensitive() {
        // Fail if service is down. No delay.
        if (isDownNow()) {
            Util.sleepRandom(500, 1000);
            throw new CatchphraseException("CatchphraseService: Too many requests");
        }

        // Start downtime on high rate
        if (!isDownNow() && isOverRateLimit()) {
            int downtimeDurationSeconds = ThreadLocalRandom.current().nextInt(10);
            downtimeEnd = LocalDateTime.now().plus(Duration.ofSeconds(downtimeDurationSeconds));
            throw new CatchphraseException("CatchphraseService: Too many requests");
        }

        // Response with random delay. Low response time.
        Util.sleepRandom(500, 1000);
        int randomIndex = ThreadLocalRandom.current().nextInt(Util.catchphrases.size());
        return Util.catchphrases.get(randomIndex);
    }

    // 3. CircuitBreaker

    public String getCatchphraseRandomDowntime() {
        // Fail if service is down. No delay.
        if (isDownNow()) {
            Util.sleepRandom(500, 1000);
            throw new CatchphraseException("CatchphraseService: Downtime");
        }

        // Start downtime randomly
        if (!isDownNow() && Util.rollDice(3, 100)) {
            int downtimeDurationSeconds = 5 + ThreadLocalRandom.current().nextInt(5);
            downtimeEnd = LocalDateTime.now().plus(Duration.ofSeconds(downtimeDurationSeconds));
            throw new CatchphraseException("CatchphraseService: Downtime");
        }

        // Response with random delay. Low response time.
        Util.sleepRandom(500, 1000);
        int randomIndex = ThreadLocalRandom.current().nextInt(Util.catchphrases.size());
        return Util.catchphrases.get(randomIndex);
    }

    // 4. Retry

    public String getCatchphraseRandomFail() {
        // Fail randomly
        if (Util.rollDice(20, 100)) {
            throw new CatchphraseException("CatchphraseService: Fail");
        }

        // Response with random delay. Low response time.
        Util.sleepRandom(500, 1000);
        int randomIndex = ThreadLocalRandom.current().nextInt(Util.catchphrases.size());
        return Util.catchphrases.get(randomIndex);
    }

    private static RateLimiter initInternalRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofSeconds(1))
                .build();

        return RateLimiter.of("catchphrase-internal", config);
    }

    private boolean isOverRateLimit() {
        try {
            rateCounter.get();
        } catch (RequestNotPermitted e) {
            return true;
        }
        return false;
    }

    private boolean isDownNow() {
        return LocalDateTime.now().isBefore(downtimeEnd);
    }
}
